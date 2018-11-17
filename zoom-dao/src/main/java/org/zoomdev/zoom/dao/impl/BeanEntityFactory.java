package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.Converter;
import org.zoomdev.zoom.common.utils.MapUtils;
import org.zoomdev.zoom.dao.AutoGenerateValue;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.DataAdapter;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.annotations.*;
import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.auto.AutoGenerateValueUsingFactory;
import org.zoomdev.zoom.dao.auto.DatabaseAutoGenerateKey;
import org.zoomdev.zoom.dao.auto.SequenceAutoGenerateKey;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.JoinMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

class BeanEntityFactory extends AbstractEntityFactory {

    private List<ContextHandler> handlers;

    protected BeanEntityFactory(Dao dao) {
        super(dao);
        handlers = new ArrayList<ContextHandler>();

        handlers.add(new ValueCasterContextHandler(
                new ValueCasterCreator1(),
                new ValueCasterCreator2(),
                new ValueCasterCreator3(),
                new ValueCasterCreator4()
        ));

        handlers.add(new StatementAdapterContextHandler(
                new StatementAdapterCreator1(),
                new StatementAdapterCreator2()

        ));


        handlers.add(new FillContextHandldr());


    }

    interface BeanContextHandler extends ContextHandler<CreateContext>{

    }


    class FillContextHandldr implements BeanContextHandler {

        @Override
        public void handle(AbstractEntityField entityField, CreateContext context) {
            if(context.config!=null){
                ColumnMeta columnMeta = context.config.columnMeta;
                entityField.setOriginalFieldName(columnMeta.getName());
                entityField.setColumn(context.config.columnName);
                entityField.setSelectColumnName(context.config.selectColumnName);
                entityField.setColumnMeta(columnMeta);
            }else{
                if(context.column!=null && !context.column.value().isEmpty()){
                    entityField.setColumn(context.column.value());
                    entityField.setSelectColumnName(context.column.value());
                }else{
                    throw new DaoException("绑定实体类出错，找不到字段的配置" + context.field+"当前所有可用字段为" +
                    StringUtils.join(context.getAvliableFields(),","));
                }
            }
        }
    }

    class ValueCasterContextHandler implements BeanContextHandler {

        public ValueCasterContextHandler(ValueCasterCreator... creators) {
            this.creators = creators;
        }

        private ValueCasterCreator[] creators;

        @Override
        public void handle(AbstractEntityField field, CreateContext context) {
            for (ValueCasterCreator<CreateContext> creator : creators) {
                ValueCaster valueCaster = creator.create(context);
                if (valueCaster != null) {
                    field.setCaster(valueCaster);
                    break;
                }
            }
        }
    }

    class StatementAdapterContextHandler implements BeanContextHandler {

        public StatementAdapterContextHandler(StatementAdapterCreator... creators) {
            this.creators = creators;
        }

        private StatementAdapterCreator[] creators;

        @Override
        public void handle(AbstractEntityField field, CreateContext context) {
            for (StatementAdapterCreator<CreateContext> creator : creators) {
                StatementAdapter statementAdapter = creator.create(context);
                if (statementAdapter != null) {
                    field.setStatementAdapter(statementAdapter);
                    break;
                }
            }
        }
    }

    static RenameUtils.ColumnRenameConfig search(Map<String, RenameUtils.ColumnRenameConfig> map, String column) {
        for (Map.Entry<String, RenameUtils.ColumnRenameConfig> entry : map.entrySet()) {
            String key = entry.getKey();
            if (column.equalsIgnoreCase(key)) {
                return entry.getValue();
            }

            RenameUtils.ColumnRenameConfig config = entry.getValue();
            if (column.equalsIgnoreCase(config.columnMeta.getName())) {
                return config;
            }


            if (column.equalsIgnoreCase(config.columnName)) {
                return config;
            }

        }
        return null;
    }

    class CreateContext {
        Field field;
        Column column;
        RenameUtils.ColumnRenameConfig config;
        DataAdapter dataAdapter;
        Type[] types;
        Map<String,RenameUtils.ColumnRenameConfig> map;

        public Class<?> getDbType() {
            return config == null ? null : config.columnMeta.getDataType();
        }


        public Set<String> getAvliableFields(){
            return map.keySet();
        }

        CreateContext(Field field, Map<String, RenameUtils.ColumnRenameConfig> map) throws Exception {
            this.map = map;
            this.field = field;
            this.column = field.getAnnotation(Column.class);
            if (this.column != null && this.column.adapter() != DataAdapter.class) {
                Type[] types = Classes.getAllParameterizedTypes(column.adapter());
                if (types == null) {
                    throw new DaoException(column.adapter() + "必须是泛型类型");
                }
                if (types.length < 2) {
                    throw new DaoException(column.adapter() + "必须有至少两个泛型类型");
                }
                this.dataAdapter = this.column.adapter().newInstance();
                this.types = types;
            }

            if (this.column != null && !this.column.value().isEmpty()) {
                //映射的字段
                //看下是否是 aaa_bbbb 或者 table1.aaa_bbb的形式，如果是的话，那么就在map中搜索对应的字段,注意搜索的时候忽略大小写
                String value = column.value();
                if (EntitySqlUtils.TABLE_AND_COLUMN_PATTERN.matcher(value).matches()) {
                    //搜索带点好的
                    RenameUtils.ColumnRenameConfig config = search(map, value);
                    this.config = config;

                }
            } else {
                this.config = map.get(field.getName());
            }

        }

    }



    class StatementAdapterCreator1 implements StatementAdapterCreator<CreateContext> {

        @Override
        public StatementAdapter create(CreateContext context) {
            if (context.dataAdapter != null) {
                return createWrappedStatementAdapter(context.dataAdapter,
                        dao.getStatementAdapter(Classes.getClass(context.types[1]), context.getDbType()));
            }
            return null;

        }
    }

    class StatementAdapterCreator2 implements StatementAdapterCreator<CreateContext> {

        @Override
        public StatementAdapter create(CreateContext context) {
            if (context.dataAdapter == null) {
                return dao.getStatementAdapter(context.field.getType(), context.getDbType());

            }
            return null;

        }
    }


    class ValueCasterCreator1 implements ValueCasterCreator<CreateContext> {

        @Override
        public ValueCaster create(CreateContext context) {
            if (context.dataAdapter != null && context.config != null) {
                return createWrappedValueCaster(context.dataAdapter,
                        Caster.wrapType(context.config.columnMeta.getDataType(),
                                context.types[1]));
            }
            return null;
        }
    }

    class ValueCasterCreator2 implements ValueCasterCreator<CreateContext> {

        @Override
        public ValueCaster create(CreateContext context) {
            if (context.dataAdapter != null && context.config == null) {
                return createWrappedValueCaster(context.dataAdapter,
                        Caster.wrapFirstVisit(context.types[1]));
            }
            return null;
        }
    }

    class ValueCasterCreator3 implements ValueCasterCreator<CreateContext> {

        @Override
        public ValueCaster create(CreateContext context) {
            if (context.dataAdapter == null && context.config == null) {
                return Caster.wrapFirstVisit(context.field.getGenericType());
            }
            return null;
        }
    }

    class ValueCasterCreator4 implements ValueCasterCreator<CreateContext> {

        @Override
        public ValueCaster create(CreateContext context) {
            if (context.dataAdapter == null && context.config != null) {
                return Caster.wrapType(context.config.columnMeta.getDataType(), context.field.getGenericType());
            }
            return null;
        }
    }


    public Entity getEntity(final Class<?> type) {
        Table table = type.getAnnotation(Table.class);
        if (table == null) {
            throw new DaoException("找不到Table标注，不能使用本方法绑定实体");
        }
        String tableName = table.value();
        assert(!tableName.isEmpty());
        Link link = type.getAnnotation(Link.class);
        Map<String, RenameUtils.ColumnRenameConfig> map;
        Join[] joins = null;
        if (link != null) {
            joins = link.value();
            //表
            String[] tables = new String[joins.length+1];
            tables[0] = tableName;
            int index = 1;
            for (Join join : joins) {
                tables[index++]=join.table();
            }
            map = RenameUtils.rename(dao, tables);
        } else {
            map = RenameUtils.rename(dao, tableName);
        }

        RenameUtils.ColumnRenameConfig firstTable = map.values().iterator().next();
        TableMeta tableMeta = firstTable.tableMeta;

        Field[] fields = CachedClasses.getFields(type);
        List<AbstractEntityField> entityFields = new ArrayList<AbstractEntityField>(fields.length);

        for (int index = 0; index < fields.length; ++index) {
            Field field = fields[index];
            if (field.isAnnotationPresent(ColumnIgnore.class)) {
                continue;
            }
            field.setAccessible(true);
            BeanEntityField entityField = new BeanEntityField(field);
            entityFields.add(entityField);

            try {
                CreateContext context = new CreateContext(field,map);

                for(ContextHandler handler : handlers){
                    handler.handle(entityField,context);
                }

            } catch (Exception e) {
                if(e instanceof DaoException){
                    throw (DaoException)e;
                }
                throw new DaoException("绑定Entity失败，发生异常:"+type,e);
            }
        }

        return new BeanEntity(
                tableName,
                entityFields.toArray(new EntityField[entityFields.size()]),
                findPrimaryKeys(entityFields, tableMeta.getColumns(), fields),
                findAutoGenerateFields(entityFields, tableMeta.getColumns(), fields),
                type,
                getNamesMap(map, fields),
                getJoinConfigs(joins));

    }



    /**
     * 在 where/groupGy等语句中的映射关系,除了fields以外的
     *
     * @param map
     * @param fields
     * @return
     */
    private Map<String, String> getNamesMap(Map<String, RenameUtils.ColumnRenameConfig> map, Field[] fields) {

        for (Field field : fields) {
            map.remove(field.getName());
        }

        return MapUtils.convert(map, new Converter<RenameUtils.ColumnRenameConfig, String>() {
            @Override
            public String convert(RenameUtils.ColumnRenameConfig data) {
                return data.columnName;
            }
        });
    }


    private JoinMeta[] getJoinConfigs(Join[] joins) {
        if(joins==null)return null;
        JoinMeta[] joinMetas = new JoinMeta[joins.length];
        for (int i = 0; i < joins.length; ++i) {
            Join join = joins[i];
            //
            JoinMeta joinMeta = new JoinMeta();
            joinMeta.setOn(join.on());
            joinMeta.setTable(join.table());
            joinMeta.setType(join.type());

            joinMetas[i] = joinMeta;
        }
        return joinMetas;
    }




    private AutoEntity findAutoGenerateFields(
            List<AbstractEntityField> entityFields,
            ColumnMeta[] columnMetas,
            Field[] fields) {
        List<AutoField> autoFields = new ArrayList<AutoField>();
        List<EntityField> autoRelatedEntityFields = new ArrayList<EntityField>();
        for (int i = 0, c = entityFields.size(); i < c; ++i) {
            AbstractEntityField entityField = entityFields.get(i);
            AutoField autoField = checkAutoField(fields[i], entityField);
            entityField.setAutoField(autoField);
            if (autoField != null) {
                autoFields.add(autoField);
                autoRelatedEntityFields.add(entityField);
            }
        }


        AutoEntity autoEntity = createAutoEntity(autoFields, autoRelatedEntityFields);
        return autoEntity;
    }


    private EntityField[] findPrimaryKeys(List<AbstractEntityField> entityFields,
                                          ColumnMeta[] columnMetas,
                                          Field[] fields) {
        List<EntityField> primaryKeys = new ArrayList<EntityField>(3);
        for (int i = 0, c = fields.length; i < c; ++i) {
            Field field = fields[i];
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                primaryKeys.add(entityFields.get(i));
            }
        }
        if (primaryKeys.size() == 0) {
            for (int i = 0, c = entityFields.size(); i < c; ++i) {

                AbstractEntityField entityField = entityFields.get(i);
                if (entityField.getColumnMeta() != null
                        && entityField.getColumnMeta().isPrimary()) {
                    primaryKeys.add(entityField);
                }
            }
        }

        return primaryKeys.toArray(new EntityField[primaryKeys.size()]);

    }

    private AutoEntity createAutoEntity(List<AutoField> autoFields, List<EntityField> entityFields) {
        List<String> generatedKeys = new ArrayList<String>();
        List<EntityField> generatedEntityFields = new ArrayList<EntityField>();
        for (int i = 0, c = autoFields.size(); i < c; ++i) {
            AutoField autoField = autoFields.get(i);
            EntityField entityField = entityFields.get(i);
            if (autoField.isDatabaseGeneratedKey()) {
                generatedKeys.add(entityField.getColumnName());
                generatedEntityFields.add(entityField);
            }
        }
        if (generatedKeys.size() > 0) {
            return new ZoomAutoEntity(generatedKeys.toArray(new String[generatedKeys.size()]),
                    generatedEntityFields.toArray(new EntityField[generatedEntityFields.size()]));
        }
        return null;

    }

    /**
     * 检查一下自增长的key
     *
     * @param field
     * @param entityField
     */
    private AutoField checkAutoField(Field field, AbstractEntityField entityField) {
        AutoGenerate autoGenerate = field.getAnnotation(AutoGenerate.class);
        if (autoGenerate != null) {
            if (autoGenerate.factory() != AutoGenerateValue.class) {
                //有factory
                try {
                    return new AutoGenerateValueUsingFactory(
                            // 显示直接初始化，后期在加入ioc容器
                            autoGenerate.factory().newInstance()
                    );
                } catch (Exception e) {
                    throw new DaoException("不能初始化" + autoGenerate.factory());
                }
            }


            if (!autoGenerate.sequence().isEmpty()) {
                return new SequenceAutoGenerateKey(autoGenerate.sequence());
            }

            return new DatabaseAutoGenerateKey();
        }


        return null;
    }


    private ValueCaster createWrappedValueCaster(
            final DataAdapter dataAdapter, final ValueCaster valueCaster) {

        return new ValueCaster() {
            @Override
            public Object to(Object src) {
                Object data = valueCaster.to(src);
                //有可能为null，注意判断
                return dataAdapter.toEntityValue(data);
            }
        };
    }


    private StatementAdapter createWrappedStatementAdapter(final DataAdapter dataAdapter, final StatementAdapter statementAdapter) {
        return new StatementAdapter() {
            @Override
            public void adapt(PreparedStatement statement, int index, Object value) throws SQLException {
                //先转化格式
                value = dataAdapter.toDbValue(value);

                statementAdapter.adapt(statement, index, value);
            }
        };
    }

    private static final String ERROR_FORMAT = "解析实体类出错[%s]:[%s]";

    private String parseColumn(String column, int index) {

        Matcher matcher;
        if ((matcher = BuilderKit.AS_PATTERN.matcher(column)).matches()) {
            //直接用原始的
            return column;
        } else {
            //用自动命名,不管是什么都是用AS
            return String.format("%s AS F%d_", column, index);
        }
    }
}
