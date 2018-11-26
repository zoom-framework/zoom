package org.zoomdev.zoom.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.alias.AliasPolicyFactory;
import org.zoomdev.zoom.dao.alias.NameAdapter;
import org.zoomdev.zoom.dao.alias.impl.DetectPrefixAliasPolicyFactory;
import org.zoomdev.zoom.dao.alias.impl.ToLowerCaseNameAdapter;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.driver.h2.H2DbStruct;
import org.zoomdev.zoom.dao.driver.h2.H2Driver;
import org.zoomdev.zoom.dao.driver.mysql.MysqlDbStruct;
import org.zoomdev.zoom.dao.driver.mysql.MysqlDriver;
import org.zoomdev.zoom.dao.driver.oracle.OracleDbStruct;
import org.zoomdev.zoom.dao.driver.oracle.OracleDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;
import org.zoomdev.zoom.dao.migrations.ZoomDatabaseBuilder;
import org.zoomdev.zoom.dao.transaction.Trans;
import org.zoomdev.zoom.dao.transaction.Transactions;
import org.zoomdev.zoom.dao.utils.DaoUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;

/**
 * dao
 *
 * @author jzoom
 */
public class ZoomDao implements Dao, Destroyable {

    private static final Log log = LogFactory.getLog(Dao.class);

    private SqlDriver sqlDriver;
    private DataSource dataSource;
    private EntityFactory entityFactory;


    private DbStructFactory dbStructFactory;
    private boolean lazyLoad;

    private NameAdapter nameAdapter;
    private Collection<String> names;


    private ThreadLocal<Ar> arholder = new ThreadLocal<Ar>();

    private ThreadLocal<EAr<?>> earHolder = new ThreadLocal<EAr<?>>();

    private String url;

    private boolean output;

    public ZoomDao(DataSource dataSource) {
        this(dataSource, false);
    }

    /**
     * 创建一个Dao对象
     *
     * @param dataSource
     * @param lazyLoad   是否在需要使用的时候才创建各种相关对象：如绑定实体类等,改成true，启动时间将缩减500ms左右！
     */
    public ZoomDao(DataSource dataSource, boolean lazyLoad) {
        this.dataSource = dataSource;
        this.lazyLoad = lazyLoad;
        Db.register(this);
        entityFactory = new CachedEntityFactory(new BeanEntityFactory(this), new RecordEntityFactory(this));
        this.nameAdapter = ToLowerCaseNameAdapter.DEFAULT;
        if (lazyLoad) {
            return;
        }
        load();
    }

    public void release() {
        arholder.remove();
        earHolder.remove();
    }

    @Override
    public void setNameAdapter(NameAdapter nameAdapter) {
        this.nameAdapter = nameAdapter;
    }

    @Override
    public void setOutput(boolean output) {
        this.output = output;
    }

    public static void executeTrans(Runnable runnable) {
        executeTrans(Trans.TRANSACTION_READ_COMMITTED, runnable);
    }

    public static void executeTrans(int level, Runnable runnable) {
        try {
            assert (runnable != null);
            ZoomDao.beginTrans(level);
            runnable.run();
            ZoomDao.commitTrans();
        } catch (Throwable e) {
            ZoomDao.rollbackTrans();
            if (e instanceof DaoException) {
                throw (DaoException) e;
            }
            throw new DaoException(e);
        }

    }

    @Override
    public void destroy() {
        Classes.destroy(dbStructFactory);
        Classes.destroy(entityFactory);

        Io.closeAny(dataSource);
        Db.unregister(this);
    }

    public <T> T execute(ConnectionExecutor executor) {
        return ar().execute(executor);
    }


    @Override
    public String getURL() {
        return url;
    }

    public DbStructFactory getDbStructFactory() {
        lazyLoad();
        return dbStructFactory;
    }

    @Override
    public Entity getEntity(Class<?> type) {
        return entityFactory.getEntity(type);
    }

    @Override
    public Entity getEntity(String... tables) {
        return entityFactory.getEntity(tables);
    }

    private void load() {
        Connection connection = null;
        // 需要绑定entities
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String name = metaData.getDatabaseProductName();
            log.info(String.format("检测到数据库产品名称%s", name));
            this.url = metaData.getURL();
            this.sqlDriver = createDriver(name);
            String tableCat = sqlDriver.getTableCatFromUrl(url);
            this.dbStructFactory =
                    new CachedDbStructFactory(createDbStructFactory(
                            metaData.getDatabaseProductName(), tableCat));

        } catch (SQLException e) {
            throw new DaoException("创建Dao失败,连接数据库错误", e);
        } finally {
            DaoUtils.close(connection);
        }
    }


    private DbStructFactory createDbStructFactory(String productName, String tableCat) {
        if (Databases.MYSQL.equals(productName)) {
            return new MysqlDbStruct(this, tableCat);
        }

        if (Databases.H2.equals(productName)) {
            return new H2DbStruct(this, tableCat);
        }

        if (Databases.ORACLE.equalsIgnoreCase(productName)) {
            return new OracleDbStruct(this, tableCat);
        }

        throw new RuntimeException(String.format("不支持的数据库产品:%s", productName));
    }

    private SqlDriver createDriver(String productName) {
        if (Databases.MYSQL.equals(productName)) {
            return new MysqlDriver();
        }

        if (Databases.H2.equals(productName)) {
            return new H2Driver();
        }

        if (Databases.ORACLE.equalsIgnoreCase(productName)) {
            return new OracleDriver();
        }

        throw new RuntimeException(String.format("不支持的数据库产品:%s", productName));
    }


    public Ar ar() {
        Ar ar = arholder.get();
        if (ar == null) {
            ar = createAr();
            arholder.set(ar);
        }
        return ar;
    }

    @Override
    public <T> EAr<T> ar(Class<T> type) {
        EAr<T> ar = (EAr<T>) earHolder.get();
        Entity entity = entityFactory.getEntity(type);
        if (ar == null) {
            lazyLoad();
            ar = new EntityActiveRecord<T>(this, entity,output);
            earHolder.set(ar);
        } else {
            ar.setEntity(entity);
        }
        return ar;
    }

    @Override
    public EAr<Record> ar(String... tables) {
        EAr<Record> ar = (EAr<Record>) earHolder.get();
        Entity entity = entityFactory.getEntity(tables);
        if (ar == null) {
            lazyLoad();
            ar = new EntityActiveRecord<Record>(this, entity,output);
            earHolder.set(ar);
        } else {
            ar.setEntity(entity);
        }
        return ar;
    }


    private void lazyLoad() {
        if (sqlDriver == null) {
            synchronized (this) {
                if (sqlDriver == null) {
                    load();
                }
            }
        }
    }

    private Ar createAr() {
        lazyLoad();
        return new ActiveRecord(this, nameAdapter,output);
    }


    @Override
    public Ar table(String table) {
        return ar().table(table);
    }


    public Collection<String> getTableNames() {
        return names;
    }


    private static AliasPolicyFactory maker = DetectPrefixAliasPolicyFactory.DEFAULT;

    private String[] getColumnNames(TableMeta meta) {

        String[] names = new String[meta.getColumns().length];
        int index = 0;
        for (ColumnMeta columnMeta : meta.getColumns()) {
            names[index++] = columnMeta.getName();
        }
        return names;
    }


    private static ThreadLocal<Transactions> threadLocal = new ThreadLocal<Transactions>();

    public static Transactions getTransaction() {
        return threadLocal.get();
    }

    public static Connection getConnection(DataSource dataSource) {
        try {
            Transactions transactions = getTransaction();
            if (transactions == null) {
                return dataSource.getConnection();
            }
            return transactions.getConnection(dataSource);
        } catch (SQLException e) {
            throw new DaoException("获取数据库连接失败", e);
        }

    }


    public static boolean releaseConnection(DataSource dataSource, Connection connection) {
        Transactions transactions = getTransaction();
        if (transactions == null) {
            try {
                connection.close();
            } catch (Throwable e) {
                log.error("关闭连接失败", e);
            }
            return true;
        }
        return false;
    }

    public static void beginTrans(int level) {
        Transactions transactions = getTransaction();
        if (transactions == null) {
            transactions = new Transactions(level);
            threadLocal.set(transactions);
        } else {
            transactions.addRefCount();
        }
    }

    public static void commitTrans() {
        Transactions transactions = getTransaction();
        if (transactions != null) {
            if (0 == transactions.subRefCount()) {
                try {
                    transactions.commit();
                } finally {
                    threadLocal.remove();
                }
            }
        }
    }

    public static void rollbackTrans() {
        Transactions transactions = getTransaction();
        if (transactions != null) {
            if (0 == transactions.subRefCount()) {
                try {
                    transactions.rollback();
                } finally {
                    threadLocal.remove();
                }
            }
        }
    }

    @Override
    public void clearCache() {
        dbStructFactory.clearCache();
        entityFactory.clearCache();
    }

    @Override
    public SqlDriver getDriver() {
        return sqlDriver;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public AliasPolicyFactory getAliasPolicyMaker() {
        return maker;
    }

    @Override
    public DatabaseBuilder builder() {
        lazyLoad();
        return new ZoomDatabaseBuilder(this);
    }

    @Override
    public StatementAdapter getStatementAdapter(Class<?> fieldType, Class<?> columnType) {
        StatementAdapter adapter = sqlDriver.getStatementAdapter(fieldType, columnType);
        if (adapter != null) {
            return adapter;
        }
        return StatementAdapters.create(fieldType, columnType);
    }


}
