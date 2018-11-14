package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.adapters.EntityField;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 将来与ActiveRecord合并
 *
 * @param <T>
 */
public class EntityActiveRecord<T> extends ThreadLocalConnectionHolder implements EAr<T> {

    private static final Log log = LogFactory.getLog(EntityActiveRecord.class);

    public boolean defaultIgnoreNull = true;
    public boolean defaultStrict = true;

    protected Dao dao;
    protected Entity entity;
    protected List<EntityField> entityFields;

    protected boolean ignoreNull = defaultIgnoreNull;
    protected boolean strict = defaultStrict;

    protected static Map<String, Filter<EntityField>> patterFilterCache = new ConcurrentHashMap<String, Filter<EntityField>>();
    /**
     * 对字段进行筛选
     */
    protected Filter<EntityField> filter;


    private static Filter<EntityField> createPatternFilter(final String filter) {
        return SingletonUtils.liteDoubleLockMap(
                patterFilterCache,
                filter,
                new SingletonUtils.SingletonInit<Filter<EntityField>>() {
                    @Override
                    public Filter<EntityField> create() {
                        return new EntitySqlUtils.PatterFilter(PatternFilterFactory.createFilter(filter));
                    }
                });
    }

    public EntityActiveRecord(Dao dao, Entity entity) {
        super(dao.getDataSource(), new SimpleSqlBuilder(dao.getDriver()));
        this.entity = entity;
        this.dao = dao;
        this.entityFields = new ArrayList<EntityField>();
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
    public List<T> find() {

        return execute(new ConnectionExecutor() {
            @Override
            public List<T> execute(Connection connection) throws SQLException {
                entity.setQuerySource(builder);
                EntitySqlUtils.buildSelect(builder, entity, filter, entityFields);
                builder.buildSelect();
                return EntitySqlUtils.executeQuery(connection, builder, entityFields, entity);
            }
        });
    }


    @Override
    public List<T> limit(final int position, final int size) {
        return execute(new ConnectionExecutor() {
            @Override
            public List<T> execute(Connection connection) throws SQLException {
                entity.setQuerySource(builder);
                EntitySqlUtils.buildSelect(builder, entity, filter, entityFields);
                builder.buildLimit(position, size);
                return EntitySqlUtils.executeQuery(connection, builder, entityFields, entity);
            }
        });
    }

    protected void remove2(List<Object> values){
        if(values.size()==0){
            values.clear();
            return;
        }
        values.remove(values.size()-1);
        values.remove(values.size()-1);
    }

    @Override
    public Page<T> position(final int position, final int size) {
        return execute(new ConnectionExecutor() {
            @Override
            public Page<T> execute(Connection connection) throws SQLException {
                entity.setQuerySource(builder);
                EntitySqlUtils.buildSelect(builder, entity, filter, entityFields);
                builder.buildLimit(position, size);
                List<T> list = EntitySqlUtils.executeQuery(connection, builder, entityFields, entity);
                builder.clear(false);
                remove2(builder.values);
                //最后两个参数要移除掉
                int total = getValue(connection, "COUNT(*) AS COUNT_", int.class);
                int page = builder.position2page(position, size);
                return new Page<T>(list, page, size, total);
            }
        });
    }

    @Override
    public Page<T> page(int page, int size) {
        if (page <= 0) page = 1;
        return position((page - 1) * size, size);
    }


    private void validateRecord(Record record){
        Set<String> allKeys = new HashSet<String>(record.size());
        allKeys.addAll(record.keySet());

        for(EntityField entityField : entity.getEntityFields()){
            allKeys.remove(entityField.getFieldName());
        }

        if(allKeys.size() > 0){
            throw new DaoException("Record中包含多余字段:"+
            StringUtils.join(allKeys,",")+"所有可能的字段为"+StringUtils.join(
                    entity.getAvailableFields(),","
            ));
        }

    }

    @Override
    public int update(T data) {
        assert(data!=null);
        if(data instanceof Record && strict){
            //检测一下
            validateRecord((Record)data);
        }

        entity.validate(data);
        EntitySqlUtils.entityCondition(builder, entity, data);

        EntitySqlUtils.buildUpdate(
                builder,
                dao.getDriver(),
                entity,
                data,
                filter,
                ignoreNull);

        return EntitySqlUtils.executeUpdate(
                this,
                builder
        );
    }
//
//    @Override
//    public int insertOrUpdate(String... fields) {
//        return 0;
//    }


    /**
     * @param values
     * @return
     */
    public T get(final Object... values) {
        return execute(new ConnectionExecutor() {
            @Override
            public T execute(Connection connection) throws SQLException {
                if (entity.getPrimaryKeys().length == 0) {
                    throw new DaoException("本表" + entity.getTable() + "没有主键，系统无法判断条件");
                }

                if (entity.getPrimaryKeys().length != values.length) {
                    throw new DaoException(
                            "参数个数" + values.length + "与主键个数" + entity.getPrimaryKeys().length + "不符");
                }

                int index = 0;
                for (EntityField adapter : entity.getPrimaryKeys()) {
                    builder.where(adapter.getColumnName(), values[index++]);
                }

                return getOne(connection);
            }
        });

    }

    T getOne(Connection connection) throws SQLException {
        entity.setQuerySource(builder);
        EntitySqlUtils.buildSelect(builder, entity, filter, entityFields);
        builder.buildSelect();
        return EntitySqlUtils.executeGet(connection, builder, entity, entityFields);
    }

    @Override
    public T get() {
        return execute(new ConnectionExecutor() {
            @Override
            public T execute(Connection connection) throws SQLException {

                return getOne(connection);
            }
        });
    }


    @Override
    public int insert(final T data) {
        assert(data!=null);

        if(data instanceof Record && strict){
            validateRecord((Record) data);
        }


        entity.validate(data);
        return execute(new ConnectionExecutor() {
            @Override
            public Integer execute(Connection connection) throws SQLException {
                EntitySqlUtils.buildInsert(
                        builder,
                        dao.getDriver(),
                        entity,
                        data,
                        filter,
                        ignoreNull);
                return EntitySqlUtils.executeInsert(
                        connection,
                        entity,
                        data,
                        builder
                );
            }
        });
    }



    @Override
    public int delete(T data) {
        EntitySqlUtils.entityCondition(builder, entity, data);
        builder.buildDelete();
        return EntitySqlUtils.executeUpdate(this, builder);
    }



    public int count() {
        return value("COUNT(*) AS COUNT_", int.class);
    }

    @Override
    public <E> E value(final String key, final Class<E> typeOfE) {
        return execute(new ConnectionExecutor() {
            @Override
            public E execute(Connection connection) throws SQLException {
                return getValue(connection, key, typeOfE);
            }
        });
    }

    private <E> E getValue(Connection connection, String key, Class<E> typeOfE) throws SQLException {
        builder.selectRaw(key);
        builder.buildSelect();
        return Caster.to(EntitySqlUtils.executeGetValue(connection, builder), typeOfE);
    }

    @Override
    public EAr<T> setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public EAr<T> strict(boolean strict) {
        this.strict = strict;
        return this;
    }

    @Override
    public EAr<T> orWhere(SqlBuilder.Condition condition) {
        builder.orWhere(condition);
        return this;
    }

    @Override
    public EAr<T> where(String field, Object value) {
        builder.where(entity.getColumnName(field), value);
        return this;
    }

    @Override
    public EAr<T> orderBy(String field, SqlBuilder.Sort sort) {
        builder.orderBy(entity.getColumnName(field), sort);
        return this;
    }

    @Override
    public EAr<T> groupBy(String field) {
        builder.groupBy(entity.getColumnName(field));
        return this;
    }

    @Override
    public EAr<T> having(String field, Symbol symbol, Object value) {
        builder.having(entity.getColumnName(field), symbol, value);
        return this;
    }

    @Override
    public EAr<T> union(SqlBuilder sqlBuilder) {

        SimpleSqlBuilder simpleSqlBuilder = (SimpleSqlBuilder) sqlBuilder;

        builder.sql.append(simpleSqlBuilder.sql);
        builder.values.add(simpleSqlBuilder.values);


        return this;
    }

    @Override
    public EAr<T> unionAll(SqlBuilder sqlBuilder) {
        return this;
    }

    @Override
    public EAr<T> join(String table, String on) {
        return join(table,on,SqlBuilder.INNER);
    }

    @Override
    public EAr<T> join(String table, String on, String type) {
        builder.join(table,entity.parseOn(on), type);
        return this;
    }

    @Override
    public EAr<T> select(String select) {
        filter(select.replace(",", "|"));
        return this;
    }

    @Override
    public EAr<T> select(Iterable<String> select) {
        filter(StringUtils.join(select, "|"));
        return this;
    }

    @Override
    public EAr<T> whereNull(String field) {
        builder.whereNull(entity.getColumnName(field));
        return this;
    }


    @Override
    public EAr<T> orWhere(String field, Object value) {
        builder.orWhere(entity.getColumnName(field), value);
        return this;
    }

    @Override
    public EAr<T> whereNotNull(String name) {
        builder.whereNotNull(entity.getColumnName(name));
        return this;
    }

    @Override
    public <E> EAr<T> whereIn(String field, E... values) {
        builder.whereIn(entity.getColumnName(field), values);
        return this;
    }

    @Override
    public EAr<T> like(String name, SqlBuilder.Like like, Object value) {
        builder.like(entity.getColumnName(name), like, value);
        return this;
    }

//    @Override
//    public EAr<T> whereCondition(String field, Object... values) {
//        builder.whereCondition(entity.getColumnName(field), values);
//        return this;
//    }

    @Override
    public EAr<T> where(String field, Symbol symbol, Object value) {
        builder.where(entity.getColumnName(field), symbol, value);
        return this;
    }

    @Override
    protected void clear() {
        super.clear();
        entityFields.clear();
        filter = null;
        ignoreNull = defaultIgnoreNull;
        strict = defaultStrict;
    }


//
//    @Override
//    public int insert(final Iterable<T> it) {
//        final MutableInt result = new MutableInt(0);
//        try {
//            ZoomDao.executeTrans(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            for (T t : it) {
//                                result.add(insert(t));
//                            }
//                        }
//                    });
//        } catch (Throwable throwable) {
//            throw new DaoException(throwable);
//        }
//
//        return result.getValue();
//    }
//
//    @Override
//    public int update(final Iterable<T> it) {
//        final MutableInt result = new MutableInt(0);
//        try {
//            ZoomDao.executeTrans(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            for (T t : it) {
//                                result.add(update(t));
//                            }
//                        }
//                    });
//        } catch (Throwable throwable) {
//            throw new DaoException(throwable);
//        }
//
//        return result.getValue();
//    }
//
//    @Override
//    public int delete(final Iterable<T> it) {
//        final MutableInt result = new MutableInt(0);
//        try {
//            ZoomDao.executeTrans(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            for (T t : it) {
//                                result.add(delete(t));
//                            }
//                        }
//                    });
//        } catch (Throwable throwable) {
//            throw new DaoException(throwable);
//        }
//
//        return result.getValue();
//    }
}
