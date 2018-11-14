package org.zoomdev.zoom.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.alias.NameAdapter;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyFactory;
import org.zoomdev.zoom.dao.alias.NameAdapterFactory;
import org.zoomdev.zoom.dao.alias.impl.*;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.driver.h2.H2DbStrict;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * dao
 *
 * @author jzoom
 */
public class ZoomDao implements Dao, Destroyable, NameAdapterFactory {

    private static final Log log = LogFactory.getLog(Dao.class);

    private SqlDriver sqlDriver;
    private DataSource dataSource;
    private EntityFactory beanEntityFactory;
    private DbStructFactory dbStructFactory;
    private boolean lazyLoad;

    private NameAdapterFactory nameAdapterMaker;
    private String tableCat;
    private Collection<String> names;

    private EntityFactory recordEntityFactory;

    private ThreadLocal<RawAr> arholder = new ThreadLocal<RawAr>();

    private ThreadLocal<EAr<?>> earHolder = new ThreadLocal<EAr<?>>();


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
        beanEntityFactory = new BeanEntityFactory(this);
        recordEntityFactory = new RecordEntityFactory(this);
        if (lazyLoad) {
            return;
        }
        load();
    }


    public static void executeTrans(Runnable runnable) throws Throwable {
        executeTrans(Trans.TRANSACTION_READ_COMMITTED, runnable);
    }

    public static void executeTrans(int level, Runnable runnable) throws Throwable {
        try {
            assert (runnable != null);
            ZoomDao.beginTrans(level);
            runnable.run();
            ZoomDao.commitTrans();
        } catch (Throwable e) {
            ZoomDao.rollbackTrans();
            throw e;
        }

    }

    @Override
    public void destroy() {
        Classes.destroy(dbStructFactory);
        Classes.destroy(beanEntityFactory);
        Classes.destroy(recordEntityFactory);

        Io.closeAny(dataSource);
        Db.unregister(this);
    }

    public <T> T execute(ConnectionExecutor executor) {
        return ar().execute(executor);
    }

    public DbStructFactory getDbStructFactory() {
        lazyLoad();
        return dbStructFactory;
    }

    private void load() {
        Connection connection = null;
        // 需要绑定entities
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String name = metaData.getDatabaseProductName();
            log.info(String.format("检测到数据库产品名称%s", name));
            SqlDriver sqlDriver = createDriver(name);
            parseDatabaseStruct(metaData, connection);
            this.sqlDriver = sqlDriver;
        } catch (SQLException e) {
            throw new RuntimeException("创建Dao失败,连接数据库错误", e);
        } finally {
            DaoUtils.close(connection);
        }
    }

    private void parseDatabaseStruct(final DatabaseMetaData metaData, Connection connection) {


        FutureTask task = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                ResultSet rs = null;
                try {
                    // 这里是个坑，居然还有不返回一直等待的情况，是连接池的原因还是因为其他的？
                    rs = metaData.getTables(null, null, null, null);
                    List<String> names = new ArrayList<String>();
                    String cat = null;
                    while (rs.next()) {
                        String name = rs.getString("TABLE_NAME");
                        cat = rs.getString("TABLE_CAT");
                        names.add(name);
                    }
                    ZoomDao.this.names = names;
                    ZoomDao.this.tableCat = cat;
                    ZoomDao.this.dbStructFactory = new CachedDbStructFactory(createDbStructFactory(metaData.getDatabaseProductName()));

                    return null;
                } catch (SQLException e) {
                    //错误
                    return e;
                } finally {
                    DaoUtils.close(rs);
                }

            }
        });
        // 这里使用一个线程启动，防止程序卡死
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        //这里等待一下
        try {
            Object data = task.get(1000, TimeUnit.MILLISECONDS);
            if (data instanceof Throwable) {
                throw new DaoException("初始化失败", (Throwable) data);
            }
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            throw new DaoException("初始化失败", e.getCause());
        } catch (TimeoutException e) {
            DaoUtils.close(connection);
            throw new DaoException("初始化失败，获取表列表超时");
        }

    }

    private DbStructFactory createDbStructFactory(String productName) {
        if (Databases.MYSQL.equals(productName)) {
            return new MysqlDbStruct(this, tableCat);
        }

        if (Databases.H2.equals(productName)) {
            return new H2DbStrict(this, tableCat);
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


    public RawAr ar() {
        RawAr ar = arholder.get();
        if (ar == null) {
            ar = createAr();
            arholder.set(ar);
        }
        return ar;
    }

    @Override
    public <T> EAr<T> ar(Class<T> type) {
        EAr<T> ar = (EAr<T>) earHolder.get();
        Entity entity = beanEntityFactory.getEntity(type);
        if (ar == null) {
            lazyLoad();
            ar = new EntityActiveRecord<T>(this, entity);
            earHolder.set(ar);
        } else {
            ar.setEntity(entity);
        }
        return ar;
    }

    @Override
    public EAr<Record> ar(String table) {
        EAr<Record> ar = (EAr<Record>) earHolder.get();
        Entity entity = recordEntityFactory.getEntity(Record.class, table);
        if (ar == null) {
            lazyLoad();
            ar = new EntityActiveRecord<Record>(this, entity);
            earHolder.set(ar);
        } else {
            ar.setEntity(entity);
        }
        return ar;
    }

    @Override
    public EAr<Record> ar(String[] tables) {
        EAr<Record> ar = (EAr<Record>) earHolder.get();
        Entity entity = recordEntityFactory.getEntity(Record.class, tables);
        if (ar == null) {
            lazyLoad();
            ar = new EntityActiveRecord<Record>(this, entity);
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

    private RawAr createAr() {
        lazyLoad();
        return new ActiveRecord(this);
    }


    @Override
    public Ar table(String table) {
        return ar().table(table);
    }

    @Override
    public Ar tables(String[] tables) {
        assert (tables != null);
        return ar().tables(tables);
    }


    public String getTableCat() {
        return tableCat;
    }

    public void setTableCat(String tableCat) {
        this.tableCat = tableCat;
    }

    public Collection<String> getTableNames() {
        return names;
    }


    private static AliasPolicyFactory maker = DetectPrefixAliasPolicyMaker.DEFAULT;

    private String[] getColumnNames(TableMeta meta) {

        String[] names = new String[meta.getColumns().length];
        int index = 0;
        for (ColumnMeta columnMeta : meta.getColumns()) {
            names[index++] = columnMeta.getName();
        }
        return names;
    }

    @Override
    public NameAdapter getNameAdapter(String table) {
        TableMeta meta = getDbStructFactory().getTableMeta(table);
        AliasPolicy aliasPolicy = maker.getAliasPolicy(getColumnNames(meta));
        if (aliasPolicy != null) {
            Map<String, String> map = new HashMap<String, String>();
            for (ColumnMeta columnInfo : meta.getColumns()) {
                map.put(aliasPolicy.getAlias(columnInfo.getName()), columnInfo.getName());
            }
            return new PrefixMapNameAdapter(aliasPolicy, map);
        }
        return CamelNameAdapter.ADAPTER;
    }

    @Override
    public NameAdapter getNameAdapter(String[] tables) {

        AliasPolicy tableAliasPolicy = maker.getAliasPolicy(tables);
        // 得到一个映射关系
        Map<String, String> field2columnMap = new LinkedHashMap<String, String>();
        Map<String, String> column2fieldMap = new LinkedHashMap<String, String>();
        Map<String, String> field2AsMap = new LinkedHashMap<String, String>();
        Map<String, String> column2OrgFieldMap = new LinkedHashMap<String, String>();
        boolean first = true;
        for (String table : tables) {
            TableMeta meta = getDbStructFactory().getTableMeta(table);
            String tableAlia = tableAliasPolicy.getAlias(table);
            // 取出每一个表的重命名策略
            AliasPolicy columnAlias = maker.getAliasPolicy(getColumnNames(meta));
            if (columnAlias == null) {
                columnAlias = CamelAliasPolicy.DEFAULT;
            }
            for (ColumnMeta column : meta.getColumns()) {
                String alias = columnAlias.getAlias(column.getName());
                //如果是第一个表，则直接使用字段名称，否则使用table.column的形式
                String fieldName = first ? alias : (tableAlia + StrKit.upperCaseFirst(alias));

                String columnName = first ? column.getName() : (table + "." + column.getName());

                String underLineName = StrKit.toUnderLine(fieldName);

                String asColumnName = table + "." + getDriver().protectColumn(column.getName())
                        + " AS "
                        + getDriver().protectColumn(underLineName + "_");

                //原始的
                field2AsMap.put(columnName, asColumnName);
                field2AsMap.put(fieldName, asColumnName);

                field2columnMap.put(fieldName, table + "." + column.getName());
                column2fieldMap.put(columnName, fieldName);

                column2OrgFieldMap.put(columnName, alias);
            }
            if (first) {
                first = false;
            }
        }

        return new MapNameAdapter(field2columnMap, column2fieldMap, field2AsMap, column2OrgFieldMap);
    }

    @Override
    public NameAdapterFactory getNameAdapterFactory() {
        return nameAdapterMaker == null ? this : nameAdapterMaker;
    }

    public void setNameAdapterMaker(NameAdapterFactory nameAdapterMaker) {
        this.nameAdapterMaker = nameAdapterMaker;
    }

    @Override
    public RawAr getAr() {
        return arholder.get();
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
            log.warn("transaction已经开始，level不会改变");
        }
    }

    public static void commitTrans() {
        Transactions transactions = getTransaction();
        if (transactions != null) {
            try {
                transactions.commit();
            } finally {
                threadLocal.remove();
            }
        }
    }

    public static void rollbackTrans() {
        Transactions transactions = getTransaction();
        if (transactions != null) {
            try {
                transactions.rollback();
            } finally {
                threadLocal.remove();
            }
        }
    }

    @Override
    public void clearCache() {
        dbStructFactory.clearCache();
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

    @Override
    public StatementAdapter getStatementAdapter(Class<?> columnType) {
        StatementAdapter adapter = sqlDriver.getStatementAdapter(columnType);
        if (adapter != null) {
            return adapter;
        }
        return StatementAdapters.create(columnType);
    }
}
