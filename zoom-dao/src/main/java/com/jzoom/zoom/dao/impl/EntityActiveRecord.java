package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.common.designpattern.SingletonUtils;
import com.jzoom.zoom.common.expression.Symbol;
import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;
import com.jzoom.zoom.dao.*;
import com.jzoom.zoom.dao.adapters.EntityField;
import com.jzoom.zoom.dao.utils.DaoUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 将来与ActiveRecord合并
 *
 * @param <T>
 */
public class EntityActiveRecord<T> extends ThreadLocalConnectionHolder implements EAr<T> {

    private static final Log log = LogFactory.getLog(EntityActiveRecord.class);

    private Dao dao;
    private SimpleSqlBuilder builder;
    private Entity entity;

    private boolean ignoreNull = true;

    private Map<String, Filter<EntityField>> patterFilterCache = new ConcurrentHashMap<String, Filter<EntityField>>();
    /**
     * 对字段进行筛选
     */
    private Filter<EntityField> filter;


    static class PatterFilter implements Filter<EntityField> {

        private Filter<String> pattern;

        PatterFilter(Filter<String> pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean accept(EntityField value) {
            return pattern.accept(value.getFieldName());
        }
    }

    private Filter<EntityField> createPatternFilter(final String filter) {
        return SingletonUtils.liteDoubleLockMap(patterFilterCache, filter, new SingletonUtils.SingletonInit<Filter<EntityField>>() {
            @Override
            public Filter<EntityField> create() {
                return new PatterFilter(PatternFilterFactory.createFilter(filter));
            }
        });
    }

    public EntityActiveRecord(Dao dao, Entity entity) {
        super(dao.getDataSource());
        this.builder = new SimpleSqlBuilder(dao);
        this.entity = entity;
        this.dao = dao;
    }


    @Override
    public EAr<T> filter(String filter) {
        this.filter = createPatternFilter(filter);
        return this;
    }

    @Override
    public EAr<T> ignoreNull(boolean value) {
        ignoreNull = value;
        return this;
    }

    @Override
    public EAr<T> tables(String[] arr) {
        return this;
    }

    @Override
    public EAr<T> table(String table) {
        return this;
    }


    @Override
    public List<T> find() {
        builder.table(entity.getTable());
        buildSelect();
        builder.buildSelect();
        return query(true);
    }


    public List<T> query(boolean all) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            ps = BuilderKit.prepareStatement(
                    connection,
                    builder.sql.toString(), builder.values);
            rs = ps.executeQuery();
            return BuilderKit.buildList(entity, entityAdapters, entityAdaptersCount, rs);
        } catch (Throwable e) {
            throw new DaoException(builder.printSql(), e);
        } finally {
            DaoUtils.close(rs);
            DaoUtils.close(ps);
            if (all) {
                releaseConnection();
            }
            builder.clear(all);
        }
    }

    @Override
    public List<T> limit(int position, int pageSize) {
        buildSelect();
        builder.buildLimit(position, pageSize);

        return query(true);
    }

    @Override
    public Page<T> position(int position, int pageSize) {
        buildSelect();
        builder.buildLimit(position, pageSize);
        try {
            List<T> list = query(false);
            int total = getCount();
            int page = builder.getPageFromPosition(position, pageSize);
            return new Page<T>(list, page, pageSize, total);
        } finally {
            releaseConnection();
            builder.clear(true);
        }
    }

    @Override
    public Page<T> page(int page, int pageSize) {
        if (page <= 0)
            page = 1;
        return position((page - 1) * pageSize, pageSize);
    }

    public int executeUpdate() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug(builder.printSql());
            }
            connection = getConnection();
            ps = BuilderKit.prepareStatement(connection,
                    builder.sql.toString(),
                    builder.values,
                    builder.adapters);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(builder.printSql(), e);
        } finally {
            DaoUtils.close(ps);
            releaseConnection();
            builder.clear(true);
        }
    }

    void entityConditon(Object data) {
        // 主键
        for (EntityField adapter : entity.getPrimaryKeys()) {
            builder.where(adapter.getColumnName(), adapter.get(data));
            builder.adapters.add(adapter);
        }
    }

    @Override
    public int update(T data) {
        entityConditon(data);

        BuilderKit.buildUpdate(entity,
                builder.sql,
                builder.values,
                dao.getDriver(),
                builder.where,
                data,
                filter,
                builder.adapters,
                ignoreNull);


        return executeUpdate();
    }

    @Override
    public int insertOrUpdate(String... fields) {
        return 0;
    }


    private EntityField[] entityAdapters;
    private int entityAdaptersCount;


    private void buildSelect() {
        //build select
        EntityField[] adapters = new EntityField[entity.getFieldCount()];
        int count = 0;
        for (EntityField adapter : entity.getEntityFields()) {
            if (filter == null || filter.accept(adapter)) {
                adapters[count++] = adapter;
                builder.selectRaw(adapter.getSelectName());
            }
        }
        if (count == 0) {
            throw new DaoException("必须指定至少一个selet字段");
        }
        this.entityAdapters = adapters;
        this.entityAdaptersCount = count;
    }

    private T getOne() {
        builder.table(entity.getTable());
        buildSelect();
        builder.buildSelect();
        Connection connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            ps = BuilderKit.prepareStatement(
                    connection,
                    builder.sql.toString(),
                    builder.values);
            rs = ps.executeQuery();
            if (rs.next()) {
                return BuilderKit.build(entity, entityAdapters, entityAdaptersCount, rs);
            }
            return null;
        } catch (Throwable e) {
            throw new DaoException(builder.printSql(), e);
        } finally {
            DaoUtils.close(rs);
            DaoUtils.close(ps);
            releaseConnection();
            builder.clear(true);
        }
    }

    /**
     * @param values
     * @return
     */
    public T get(Object... values) {

        if (entity.getPrimaryKeys().length == 0) {
            throw new DaoException("本表" + entity.getTable() + "没有主键，系统无法判断条件");
        }

        if (entity.getPrimaryKeys().length != values.length) {
            throw new DaoException("参数个数" + values.length + "与主键个数" + entity.getPrimaryKeys().length + "不符");
        }

        int index = 0;
        for (EntityField adapter : entity.getPrimaryKeys()) {
            builder.where(adapter.getColumnName(), values[index++]);
        }

        return getOne();

    }

    @Override
    public T get() {
        if (builder.where.length() == 0) {
            throw new DaoException("单独查询一个实体至少需要指定一个条件");
        }
        return getOne();

    }

    public int executeInsert(
            Object data
    ) {
        Connection connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            ps = entity.prepareInsert(connection, builder.sql.toString());

            BuilderKit.prepareStatement(
                    ps,
                    builder.values,
                    builder.adapters);
            int ret = ps.executeUpdate();
            if (ret > 0) {
                entity.afterInsert(data, ps);
            }
            return ret;
        } catch (Throwable e) {
            throw new DaoException(builder.printSql(), e);
        } finally {
            DaoUtils.close(ps);
            releaseConnection();
            builder.clear(true);
        }
    }


    @Override
    public int insert(T data) {
        BuilderKit.buildInsert(builder.sql, builder.adapters, builder.values, dao.getDriver(),
                entity, data, filter, ignoreNull);

        return executeInsert(data);
    }

    @Override
    public int insert(final Iterable<T> it) {
        final MutableInt result = new MutableInt(0);
        try {
            ZoomDao.runTrans(new Runnable(){
                @Override
                public void run() {
                    for(T t : it){
                        result.add(insert(t));
                    }
                }
            });
        } catch (Throwable throwable) {
            throw new DaoException(throwable);
        }

        return result.getValue();
    }

    @Override
    public int update(final Iterable<T> it) {
        final MutableInt result = new MutableInt(0);
        try {
            ZoomDao.runTrans(new Runnable(){
                @Override
                public void run() {
                    for(T t : it){
                        result.add(update(t));
                    }
                }
            });
        } catch (Throwable throwable) {
            throw new DaoException(throwable);
        }

        return result.getValue();
    }


    @Override
    public int delete(T data) {
        entityConditon(data);
        builder.buildDelete();
        return executeUpdate();
    }

    @Override
    public int delete(final Iterable<T> it) {
        final MutableInt result = new MutableInt(0);
        try {
            ZoomDao.runTrans(new Runnable() {
                @Override
                public void run() {
                    for (T t : it) {
                        result.add(delete(t));
                    }
                }
            });
        } catch (Throwable throwable) {
            throw new DaoException(throwable);
        }

        return result.getValue();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public EAr<T> where(String field, Object value) {
        return this;
    }

    @Override
    public EAr<T> orderBy(String field, SqlBuilder.Sort sort) {
        return this;
    }


    @Override
    public EAr<T> join(String table, String on) {
        return this;
    }

    @Override
    public EAr<T> orWhere(String field, Object value) {
        return this;
    }

    @Override
    public <E> EAr<T> whereIn(String field, E... values) {
        return this;
    }

    @Override
    public EAr<T> like(String name, SqlBuilder.Like like, Object value) {
        return this;
    }

    @Override
    public EAr<T> whereCondition(String field, Object... values) {
        return this;
    }

    @Override
    public EAr<T> where(String field, Symbol symbol, Object value) {
        return this;
    }
}
