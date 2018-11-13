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
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.JoinMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.utils.DaoUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BeanEntityFactory extends AbstractEntityFactory {

    protected BeanEntityFactory(Dao dao) {
        super(dao);
    }

    private static final Pattern AND_OR_PATTERN = Pattern.compile("[\\s]+(and)[\\s]+|[\\s]+(or)[\\s]+", Pattern.CASE_INSENSITIVE);

    /**
     * 使用Pattern对字符串分割
     *
     * @param target
     * @param pattern
     * @return
     */
    public static List<String> split(String target, Pattern pattern) {
        assert (target != null && pattern != null);
        List<String> list = new ArrayList<String>();
        Matcher matcher = pattern.matcher(target);
        int start = 0;
        while (matcher.find()) {
            list.add(target.substring(start, matcher.start()));
            list.add(matcher.group(1));
            start = matcher.end();
        }

        list.add(target.substring(start));

        return list;

    }


    static final Pattern COLUMN_PATTERN = Pattern.compile("[a-zA-Z0-9]+[\\s]*\\.[\\s]*[a-zA-Z0-9]+|[a-zA-Z0-9]+");

    private Set<String> getJoinAllFields(Map<String, ColumnConfig> map) {

        Set<String> allJoinAvaliableNames = new LinkedHashSet<String>();

        for (Map.Entry<String, ColumnConfig> entry : map.entrySet()) {
            ColumnConfig config = entry.getValue();
            ColumnMeta columnMeta = config.columnMeta;
            if (DaoUtils.isStream(columnMeta.getDataType())) {
                continue;
            }
            allJoinAvaliableNames.add(entry.getKey());

            allJoinAvaliableNames.add(config.getFullColumnName());
        }

        return allJoinAvaliableNames;
    }

    private void parseOneForOne(final StringBuilder sb, String part, final Map<String, ColumnConfig> map, final Set<String> joinAllFields) {

        PatternUtils.visit(part, COLUMN_PATTERN, new PatternUtils.PatternVisitor() {
            @Override
            public void onGetPattern(Matcher matcher) {
                String str = matcher.group();
                if (str.contains(".")) {
                    //table + column
                    str = str.replace(" ", "");
                    if (!joinAllFields.contains(str)) {
                        throw new DaoException("找不到" + str + "对应的字段，当前所有可用字段为:"
                                + StringUtils.join(joinAllFields, ","));
                    }
                    sb.append(str);
                } else {
                    ColumnConfig columnConfig = map.get(str);
                    if (columnConfig == null) {
                        throw new DaoException("找不到" + str + "对应的字段，当前所有可用字段为:"
                                + StringUtils.join(joinAllFields, ","));
                    } else {
                        sb.append(columnConfig.getFullColumnName());
                    }
                }
            }

            @Override
            public void onGetRest(String rest) {
                sb.append(rest);
            }
        });

    }


    private String parseOn(String on, final Map<String, ColumnConfig> map, final Set<String> joinAllFields) {
        if (StringUtils.isEmpty(on)) {
            throw new DaoException("请提供join的条件on");
        }
        final StringBuilder sb = new StringBuilder();
        PatternUtils.visit(on, AND_OR_PATTERN, new PatternUtils.PatternVisitor() {
            @Override
            public void onGetPattern(Matcher matcher) {
                sb.append(matcher.group());
            }

            @Override
            public void onGetRest(String rest) {
                parseOneForOne(sb, rest, map, joinAllFields);
            }
        });
        //再来做一次替换


        return sb.toString();
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
            return getEntity(type, tables.toArray(new String[tables.size()]), joins);
        } else {
            assert (!StringUtils.isEmpty(table.value()));
            return getEntity(type, tableName);
        }


    }

    @Override
    public Entity getEntity(final Class<?> type, final String tableName) {

        TableMeta tableMeta = getTableMeta(tableName);

        final Map<String, ColumnMeta> map = new LinkedHashMap<String, ColumnMeta>(tableMeta.getColumns().length);
        RenameUtils.rename(dao, tableName, new RenameUtils.ColumnRenameVisitor() {
            @Override
            public void visit(TableMeta tableMeta, ColumnMeta columnMeta, String fieldName, String selectColumnName) {
                map.put(fieldName, columnMeta);
            }
        });


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
                ColumnMeta columnMeta = map.get(field.getName());
                if (columnMeta == null) {
                    throw new DaoException(String.format(
                            ERROR_FORMAT,
                            field,
                            "找不到字段对应的ColumnMeta，当前所有能使用的名称为:" + StringUtils.join(map.keySet(), ",")));
                }
                fillWithoutColumnAnnotationName(
                        entityField,
                        field,
                        tableMeta,
                        columnMeta,
                        columnMeta.getName()
                );

                fillAdapter(entityField, column, columnMeta, dao, field);
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
    private Map<String, String> getNamesMap(Map<String, ColumnMeta> map, Field[] fields) {

        for (Field field : fields) {
            map.remove(field.getName());
        }

        return MapUtils.convert(map, new Converter<ColumnMeta, String>() {
            @Override
            public String convert(ColumnMeta data) {
                return data.getName();
            }
        });
    }

    public Entity getEntity(Class<?> type, String[] tables, Join[] joins) {
        assert (tables.length > 1);
        final Map<String, ColumnConfig> map = new LinkedHashMap<String, ColumnConfig>();
        final TableMeta[] tableMetas = new TableMeta[tables.length];
        RenameUtils.rename(dao, tables, new RenameUtils.ColumnRenameVisitor() {
            @Override
            public void visit(TableMeta tableMeta, ColumnMeta columnMeta, String fieldName, String selectColumnName) {
                if (tableMetas[0] == null) {
                    tableMetas[0] = tableMeta;
                }
                //做一个映射
                map.put(fieldName, new ColumnConfig(
                        tableMeta,
                        columnMeta,
                        selectColumnName
                ));
            }
        });


        JoinMeta[] joinMetas = new JoinMeta[joins.length];
        Set<String> joinAllFields = getJoinAllFields(map);
        for (int i = 0; i < joins.length; ++i) {
            Join join = joins[i];
            //
            JoinMeta joinMeta = new JoinMeta();
            joinMeta.setOn(parseOn(join.on(), map, joinAllFields));
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
                ColumnConfig columnConfig = map.get(field.getName());
                if (columnConfig == null) {
                    throw new DaoException(String.format(
                            ERROR_FORMAT,
                            field,
                            "找不到字段对应的ColumnMeta，当前所有能使用的名称为:" + StringUtils.join(map.keySet(), ",")));
                }
                fillWithoutColumnAnnotationName(
                        entityField,
                        field,
                        columnConfig
                );

                fillAdapter(entityField, column, columnConfig.columnMeta, dao, field);
            }

        }
        return new BeanEntity(
                tables[0],
                entityFields.toArray(new EntityField[entityFields.size()]),
                findPrimaryKeys(entityFields, tableMetas[0].getColumns(), fields),
                findAutoGenerateFields(entityFields, tableMetas[0].getColumns(), fields),
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
    private Map<String, String> getMultiTableNamesMap(Map<String, ColumnConfig> map, Field[] fields) {

        for (Field field : fields) {
            map.remove(field.getName());
        }

        return MapUtils.convert(map, new Converter<ColumnConfig, String>() {
            @Override
            public String convert(ColumnConfig data) {
                return data.tableMeta.getName() + ":" + data.columnMeta.getName();
            }
        });
    }

    /**
     * 多个表实际上会在实体类中提现，这里如果出现了多张表，表示和实体类中出现的多表一一对应
     *
     * @param type
     * @param tables
     * @return
     */
    @Override
    public Entity getEntity(Class<?> type, String[] tables) {
        return getEntity(type, tables, null);
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

        entityField.setColumn(tableMeta.getName() + "." + columnMeta.getName());
        entityField.setSelectColumnName(selectColumnName);
    }

    private void fillWithoutColumnAnnotationName(
            AbstractEntityField entityField,
            Field field,
            ColumnConfig columnConfig
    ) {

        entityField.setColumn(columnConfig.tableMeta.getName() + "." + columnConfig.columnMeta.getName());
        entityField.setSelectColumnName(columnConfig.selectColumnName);
    }

    private static class ColumnConfig {
        TableMeta tableMeta;
        ColumnMeta columnMeta;
        String selectColumnName;

        public ColumnConfig(TableMeta tableMeta, ColumnMeta columnMeta, String selectColumnName) {
            this.tableMeta = tableMeta;
            this.columnMeta = columnMeta;
            this.selectColumnName = selectColumnName;
        }

        public String getFullColumnName() {
            return String.format("%s.%s", tableMeta.getName(), columnMeta.getName());
        }
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

    private EntityField[] findPrimaryKeys(List<AbstractEntityField> entityFields, ColumnMeta[] columnMetas, Field[] fields) {
        List<EntityField> primaryKeys = new ArrayList<EntityField>(3);
        for (int i = 0, c = entityFields.size(); i < c; ++i) {
            Field field = fields[i];
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                primaryKeys.add(entityFields.get(i));
            }
        }
        if (primaryKeys.size() == 0) {
            for (int i = 0, c = columnMetas.length; i < c; ++i) {
                ColumnMeta columnMeta = columnMetas[i];
                if (columnMeta.isPrimary()) {
                    primaryKeys.add(entityFields.get(i));
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
        //数据库的自动无效
//        if (isAuto) {
//            //如果已经标注了auto，完全又数据库自己处理，只要指定generated key就好了
//
//            return new DatabaseAutoGenerateKey();
//        }

        //如果数据库没有标注auto，那么由用户来处理
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
            return new DatabaseAutoGenerateKey();
        }



        return null;
    }

    static class AutoGenerateValueUsingFactory extends DatabaseAutoGenerateKey {

        private AutoGenerateValue factory;

        AutoGenerateValueUsingFactory(AutoGenerateValue factory) {
            this.factory = factory;
        }


        @Override
        public Object generageValue(Object entity, EntityField entityField) {
            //当调用的时候，直接设置值
            Object value = factory.nextVal();
            entityField.set(entity, Caster.toType(value,entityField.getFieldType()));
            return value;
        }

        //不是数据库自动生成
        @Override
        public boolean isDatabaseGeneratedKey() {
            return false;
        }
    }

    static class SequenceAutoGenerateKey extends DatabaseAutoGenerateKey {

        private String sequenceName;

        public SequenceAutoGenerateKey(String sequenceName) {
            this.sequenceName = sequenceName;
        }

        @Override
        public String getSqlInsert(Object entity, EntityField entityField) {
            return String.format("(SELECT %s.NEXT_VAL() FROM DUAL)", sequenceName);
        }
    }

    static class DatabaseAutoGenerateKey implements AutoField {


        DatabaseAutoGenerateKey() {
        }

        @Override
        public boolean notGenerateWhenHasValue() {
            return false;
        }

        @Override
        public boolean isDatabaseGeneratedKey() {
            return true;
        }

        @Override
        public String getSqlInsert(Object entity, EntityField entityField) {
            return null;
        }

        @Override
        public Object generageValue(Object entity, EntityField entityField) {
            return null;
        }
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
