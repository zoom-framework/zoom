package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.common.utils.*;
import org.zoomdev.zoom.dao.*;
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
import java.util.*;
import java.util.regex.Matcher;

class BeanEntityFactory extends AbstractEntityFactory {

    protected BeanEntityFactory(Dao dao) {
        super(dao);
    }




    @Override
    public Entity getEntity(final Class<?> type) {
        Table table = type.getAnnotation(Table.class);
        if (table == null) {
            throw new DaoException("找不到Table标注，不能使用本方法绑定实体");
        }
        String tableName = table.value();
        Link link = type.getAnnotation(Link.class);
        if (link != null) {
            Join[] joins = link.value();
            //表
            List<String> tables = new ArrayList<String>();
            tables.add(tableName);
            for (Join join : joins) {
                tables.add(join.table());
            }

            assert (!StringUtils.isEmpty(table.value()));
            return getEntityJoins(type, tables.toArray(new String[tables.size()]), joins);
        } else {
            assert (!StringUtils.isEmpty(table.value()));
            return getEntityOne(type, tableName);
        }


    }


    @Override
    public Entity getEntity(final Class<?> type, String...tables) {
        return getEntityOne(type,tables[0]);
    }

    public Entity getEntityOne(final Class<?> type, final String tableName) {

        TableMeta tableMeta = getTableMeta(tableName);

        final Map<String, RenameUtils.ColumnRenameConfig> map = RenameUtils.rename(dao,tableName);


        Field[] fields = CachedClasses.getFields(type);
        assert (tableMeta.getPrimaryKeys() != null && tableMeta.getColumns() != null);


        List<AbstractEntityField> entityFields = new ArrayList<AbstractEntityField>(fields.length);

        for (int index = 0; index < fields.length; ++index) {
            Field field = fields[index];
            if (field.isAnnotationPresent(ColumnIgnore.class)) {
                continue;
            }
            field.setAccessible(true);
            BeanEntityField entityField = new BeanEntityField(field);
            entityFields.add(entityField);
            Column column = field.getAnnotation(Column.class);
            if (column != null && !column.value().isEmpty()) {
                fillWithColumnAnnotationName(
                        entityField,
                        field,
                        column,
                        index
                );

                fillAdapterWithColumnName(
                        entityField,
                        field,
                        column
                );

            } else {
                RenameUtils.ColumnRenameConfig config = map.get(field.getName());
                if (config == null) {
                    throw new DaoException(String.format(
                            ERROR_FORMAT,
                            field,
                            "找不到字段对应的ColumnMeta，当前所有能使用的名称为:" + StringUtils.join(map.keySet(), ",")));
                }
                fillWithoutColumnAnnotationName(
                        entityField,
                        field,
                        tableMeta,
                        config.columnMeta,
                        config.columnMeta.getName()
                );

                fillAdapter(entityField, column, config.columnMeta, dao, field);
            }

        }


        return new BeanEntity(
                tableName,
                entityFields.toArray(new EntityField[entityFields.size()]),
                findPrimaryKeys(entityFields, tableMeta.getColumns(), fields),
                findAutoGenerateFields(entityFields, tableMeta.getColumns(), fields),
                type,
                getNamesMap(map, fields),
                null);
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
                return data.columnMeta.getName();
            }
        });
    }

    public Entity getEntityJoins(Class<?> type, String[] tables, Join[] joins) {
        assert (tables.length > 1);
        Map<String,RenameUtils.ColumnRenameConfig> map = RenameUtils.rename(dao, tables);
        RenameUtils.ColumnRenameConfig firstTable = map.values().iterator().next();
        TableMeta tableMeta = firstTable.tableMeta;
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


        Field[] fields = CachedClasses.getFields(type);
        List<AbstractEntityField> entityFields = new ArrayList<AbstractEntityField>(fields.length);

        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if (field.isAnnotationPresent(ColumnIgnore.class)) {
                continue;
            }
            field.setAccessible(true);
            AbstractEntityField entityField = new BeanEntityField(field);
            entityFields.add(entityField);

            Column column = field.getAnnotation(Column.class);
            if (column != null && !column.value().isEmpty()) {
                fillWithColumnAnnotationName(
                        entityField,
                        field,
                        column,
                        i
                );

                fillAdapterWithColumnName(
                        entityField,
                        field,
                        column
                );

            } else {
                RenameUtils.ColumnRenameConfig columnRenameConfig = map.get(field.getName());
                if (columnRenameConfig == null) {
                    throw new DaoException(String.format(
                            ERROR_FORMAT,
                            field,
                            "找不到字段对应的ColumnMeta，当前所有能使用的名称为:" + StringUtils.join(map.keySet(), ",")));
                }

                fillWithoutColumnAnnotationName(
                        entityField,
                        field,
                        columnRenameConfig
                );

                fillAdapter(entityField, column, columnRenameConfig.columnMeta, dao, field);
            }

        }
        return new BeanEntity(
                tables[0],
                entityFields.toArray(new EntityField[entityFields.size()]),
                findPrimaryKeys(entityFields, tableMeta.getColumns(), fields),
                findAutoGenerateFields(entityFields, tableMeta.getColumns(), fields),
                type,
                getMultiTableNamesMap(map, fields),
                joinMetas);
    }

    /**
     * 在 where/groupGy等语句中的映射关系,除了fields以外的
     *
     * @param map
     * @param fields
     * @return
     */
    private Map<String, String> getMultiTableNamesMap(Map<String, RenameUtils.ColumnRenameConfig> map, Field[] fields) {

        for (Field field : fields) {
            map.remove(field.getName());
        }

        return MapUtils.convert(map, new Converter<RenameUtils.ColumnRenameConfig, String>() {
            @Override
            public String convert(RenameUtils.ColumnRenameConfig data) {
                return data.tableMeta.getName() + "." + data.columnMeta.getName();
            }
        });
    }



    private void fillAdapter(AbstractEntityField entityField, Column column, Class<?> dbType, Dao dao) {
        try {
            ValueCaster caster;
            StatementAdapter statementAdapter;
            DataAdapter adapter = column.adapter().newInstance();
            Type[] types = Classes.getAllParameterizedTypes(column.adapter());
            if (types == null) {
                throw new DaoException(column.adapter() + "必须是泛型类型");
            }
            if (types.length < 2) {
                throw new DaoException(column.adapter() + "必须有至少两个泛型类型");
            }
            caster = createWrappedValueCaster(adapter, Caster.wrapType(dbType, types[1]));
            statementAdapter = createWrappedStatementAdapter(adapter, dao.getStatementAdapter(Classes.getClass(types[0]), dbType));
            entityField.setCaster(caster);
            entityField.setStatementAdapter(statementAdapter);
        } catch (Exception e) {
            throw new DaoException("不能实例化类" + column.adapter());
        }
    }

    private void fillAdapter(
            AbstractEntityField entityField,
            Column column,
            ColumnMeta columnMeta,
            Dao dao, Field field) {
        ValueCaster caster;
        StatementAdapter statementAdapter;
        if (column != null && column.adapter() != DataAdapter.class) {
            fillAdapter(entityField, column, columnMeta.getDataType(), dao);
        } else {
            caster = Caster.wrapType(columnMeta.getDataType(), field.getGenericType());
            statementAdapter = dao.getStatementAdapter(field.getType(), columnMeta.getDataType());
            entityField.setCaster(caster);
            entityField.setStatementAdapter(statementAdapter);
        }


    }

    private void fillWithColumnAnnotationName(
            AbstractEntityField entityField,
            Field field,
            Column column,
            int index) {
        entityField.setColumn(column.value());
        entityField.setSelectColumnName(parseColumn(column.value(), index));


    }

    private void fillAdapterWithColumnName(
            AbstractEntityField entityField,
            Field field,
            Column column) {
        if (column.adapter() != DataAdapter.class) {
            fillAdapter(entityField, column, null, dao);
        } else {
            entityField.setCaster(Caster.wrapFirstVisit(field.getGenericType()));
            entityField.setStatementAdapter(StatementAdapters.DEFAULT);
        }
    }

    private void fillWithoutColumnAnnotationName(
            AbstractEntityField entityField,
            Field field,
            TableMeta tableMeta,
            ColumnMeta columnMeta,
            String selectColumnName
    ) {

        entityField.setOriginalFieldName(columnMeta.getName());
        entityField.setColumn(tableMeta.getName() + "." + columnMeta.getName());
        entityField.setSelectColumnName(selectColumnName);
        entityField.setColumnMeta(columnMeta);
    }

    private void fillWithoutColumnAnnotationName(
            AbstractEntityField entityField,
            Field field,
            RenameUtils.ColumnRenameConfig columnRenameConfig
    ) {
        entityField.setOriginalFieldName(columnRenameConfig.columnMeta.getName());
        entityField.setColumn(columnRenameConfig.tableMeta.getName() + "." + columnRenameConfig.columnMeta.getName());
        entityField.setSelectColumnName(columnRenameConfig.selectColumnName);
        entityField.setColumnMeta(columnRenameConfig.columnMeta);
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
                if(entityField.getColumnMeta()!=null
                        && entityField.getColumnMeta().isPrimary()){
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
            if(autoGenerate.factory() != AutoGenerateValue.class){
                //有factory
                try {
                    return new AutoGenerateValueUsingFactory(
                            // 显示直接初始化，后期在加入ioc容器
                            autoGenerate.factory().newInstance()
                    );
                } catch (Exception e) {
                    throw new DaoException("不能初始化"+autoGenerate.factory());
                }
            }


            if(!autoGenerate.sequence().isEmpty()){
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
