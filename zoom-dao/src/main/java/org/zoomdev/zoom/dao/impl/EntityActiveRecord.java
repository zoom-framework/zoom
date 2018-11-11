package org.zoomdev.zoom.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
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
    private List<EntityField> entityFields;

    private boolean ignoreNull = true;

    private Map<String, Filter<EntityField>> patterFilterCache =
            new ConcurrentHashMap<String, Filter<EntityField>>();
    /**
     * 对字段进行筛选
     */
    private Filter<EntityField> filter;

    @Override
    protected String printSql() {
        return builder.printSql();
    }




    private Filter<EntityField> createPatternFilter(final String filter) {
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
        super(dao.getDataSource());
        this.builder = new SimpleSqlBuilder(dao);
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
        builder.table(entity.getTable());
        EntitySqlUtils.buildSelect(builder,entity,filter,entityFields);
        builder.buildSelect();
        return EntitySqlUtils.executeQuery(this,builder,entityFields,entity,true);
    }



    @Override
    public List<T> limit(int position, int size) {
        builder.table(entity.getTable());
        EntitySqlUtils.buildSelect(builder,entity,filter,entityFields);
        builder.buildLimit(position, size);
        return EntitySqlUtils.executeQuery(this,builder,entityFields,entity,true);
    }

    @Override
    public Page<T> position(int position, int size) {
        builder.table(entity.getTable());
        EntitySqlUtils.buildSelect(builder,entity,filter,entityFields);
        builder.buildLimit(position, size);
        try {
            List<T> list = EntitySqlUtils.executeQuery(this,builder,entityFields,entity,false);
            int total = count();
            int page = builder.getPageFromPosition(position, size);
            return new Page<T>(list, page, size, total);
        } finally {
            releaseConnection();
            builder.clear(true);
        }
    }

    @Override
    public Page<T> page(int page, int size) {
        if (page <= 0) page = 1;
        return position((page - 1) * size, size);
    }


    @Override
    public int update(T data) {
        EntitySqlUtils.entityConditon(builder, entity, data);

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

    @Override
    public int insertOrUpdate(String... fields) {
        return 0;
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
            throw new DaoException(
                    "参数个数" + values.length + "与主键个数" + entity.getPrimaryKeys().length + "不符");
        }

        int index = 0;
        for (EntityField adapter : entity.getPrimaryKeys()) {
            builder.where(adapter.getColumnName(), values[index++]);
        }

        return getOne();

    }

    T getOne(){
        builder.table(entity.getTable());
        EntitySqlUtils.buildSelect(builder,entity,filter,entityFields);
        builder.buildSelect();
        return EntitySqlUtils.executeGet(this,builder,entity,entityFields);
    }

    @Override
    public T get() {
        if (builder.where.length() == 0) {
            throw new DaoException("单独查询一个实体至少需要指定一个条件");
        }
        return getOne();
    }


    @Override
    public int insert(T data) {
        EntitySqlUtils.buildInsert(
                builder,
                dao.getDriver(),
                entity,
                data,
                filter,
                ignoreNull);
        return EntitySqlUtils.executeInsert(
                this,
                entity,
                data,
                builder
        );
    }

    @Override
    public int insert(final Iterable<T> it) {
        final MutableInt result = new MutableInt(0);
        try {
            ZoomDao.runTrans(
                    new Runnable() {
                        @Override
                        public void run() {
                            for (T t : it) {
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
            ZoomDao.runTrans(
                    new Runnable() {
                        @Override
                        public void run() {
                            for (T t : it) {
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
        EntitySqlUtils.entityConditon(builder, entity, data);
        builder.buildDelete();
        return EntitySqlUtils.executeUpdate(this, builder);
    }

    @Override
    public int delete(final Iterable<T> it) {
        final MutableInt result = new MutableInt(0);
        try {
            ZoomDao.runTrans(
                    new Runnable() {
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

    public int count() {
        return value("COUNT(*) AS COUNT_",int.class);
    }

    @Override
    public <E> E value(String key, Class<E> typeOfE) {
        builder.selectRaw(key);
        builder.buildSelect();
        return Caster.to(EntitySqlUtils.executeGetValue(this,builder),int.class);
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
        builder.having(entity.getColumnName(field),symbol,value);
        return this;
    }

    @Override
    public EAr<T> union(SqlBuilder sqlBuilder) {
        return this;
    }

    @Override
    public EAr<T> unionAll(SqlBuilder sqlBuilder) {
        return this;
    }

    @Override
    public EAr<T> join(String table, String on) {
        return this;
    }

    @Override
    public EAr<T> join(String table, String on, String type) {

        return this;
    }

    @Override
    public EAr<T> select(String select) {
        filter(select.replace(",","|"));
        return this;
    }

    @Override
    public EAr<T> select(Iterable<String> select) {
        filter(StringUtils.join(select,"|"));
        return this;
    }


    @Override
    public EAr<T> orWhere(String field, Object value) {
        builder.orWhere(entity.getColumnName(field), value);
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

    @Override
    public EAr<T> whereCondition(String field, Object... values) {
        builder.whereCondition(entity.getColumnName(field), values);
        return this;
    }

    @Override
    public EAr<T> where(String field, Symbol symbol, Object value) {
        builder.where(entity.getColumnName(field), symbol, value);
        return this;
    }
}
