package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.caster.ValueCaster;
import com.jzoom.zoom.common.utils.CachedClasses;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.dao.*;
import com.jzoom.zoom.dao.adapters.DataAdapter;
import com.jzoom.zoom.dao.adapters.EntityField;
import com.jzoom.zoom.dao.adapters.NameAdapter;
import com.jzoom.zoom.dao.adapters.StatementAdapter;
import com.jzoom.zoom.dao.annotations.AutoGenerate;
import com.jzoom.zoom.dao.annotations.Column;
import com.jzoom.zoom.dao.annotations.ColumnIgnore;
import com.jzoom.zoom.dao.annotations.Table;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.TableMeta;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

class BeanEntityFactory implements EntityFactory {

    static class EntityKey{
        private Class<?> type;
        private String table;

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public EntityKey(
                @NotNull
                Class<?> type,
                @NotNull String table
        ) {
            assert (type!=null);
            assert (table!=null);
            this.type = type;
            this.table = table.toLowerCase();
        }

    }



    @Override
    public Entity getEntity(final Dao dao, final Class<?> type) {
        Table table = type.getAnnotation(Table.class);
        String tableName = table.name();
        assert (!StringUtils.isEmpty(table.name()));
        return getEntity(dao,type,tableName);
    }

    @Override
    public Entity getEntity(final Dao dao, final Class<?> type, final String tableName) {
        return bindEntity(dao, type, tableName);
    }

    /**
     * 多个表实际上会在实体类中提现，这里如果出现了多张表，表示和实体类中出现的多表一一对应
     *
     * @param dao
     * @param type
     * @param tables
     * @return
     */
    @Override
    public Entity getEntity(Dao dao, Class<?> type, String[] tables) {
        throw new UnsupportedOperationException("本操作不支持");
    }

    private void fillAdapter(BeanEntityField entityField, Column column, Class<?> dbType, Dao dao) {
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

    private void fillAdapter(BeanEntityField entityField, Column column, ColumnMeta columnMeta, Dao dao, Field field) {
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


    public Entity bindEntity(Dao dao,Class<?> type,String tableName){
        TableMeta meta = dao.getDbStructFactory().getTableMeta(dao.ar(),tableName);
        dao.getDbStructFactory().fill(dao.ar(),meta);
        NameAdapter nameAdapter = dao.getNameAdapterFactory().getNameAdapter(tableName);

        Map<String,ColumnMeta> map = new LinkedHashMap<String, ColumnMeta>(meta.getColumns().length);
        for(ColumnMeta columnMeta : meta.getColumns()){
            map.put(nameAdapter.getFieldName(columnMeta.getName()),columnMeta);
        }
        int index = 0;
        Field[] fields = CachedClasses.getFields(type);
        assert(meta.getPrimaryKeys()!=null && meta.getColumns()!=null);
        List<EntityField> entityAdapterList = new ArrayList<EntityField>(meta.getColumns().length);
        List<StatementAdapter> statementAdapters = new ArrayList<StatementAdapter>(meta.getColumns().length);
        EntityField[] primaryKeys = new EntityField[meta.getPrimaryKeys().length];
        int primaryKeyIndex = 0;

        StatementAdapter statementAdapter;
        List<AutoField> autoFields = new ArrayList<AutoField>();
        List<EntityField> autoRelatedEntityFields = new ArrayList<EntityField>();

        for ( Field field : fields ) {
            if(field.isAnnotationPresent(ColumnIgnore.class)){
                continue;
            }
            boolean isAuto = false;
            BeanEntityField entityField;
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            String selectColumn ;
            String columnName;
            ValueCaster caster;
            boolean isPrimaryKey = false;
            if (column != null && !StringUtils.isEmpty(column.value())) {
                if(StringUtils.isEmpty(column.value())){
                    throw new DaoException(String.format(ERROR_FORMAT,field,"Column标注的value为空"));
                }
                selectColumn = parseColumn(column.value(),index);
                columnName = column.value();

                entityField = new BeanEntityField(columnName, selectColumn, field);

                //////// //////// //////// //////// ////////
                if (column.adapter() != DataAdapter.class) {
                    fillAdapter(entityField, column, null, dao);
                } else {
                    caster = Caster.wrapFirstVisit(field.getGenericType());
                    statementAdapter = StatementAdapters.DEFAULT;
                    entityField.setCaster(caster);
                    entityField.setStatementAdapter(statementAdapter);
                }

            }else{
                ColumnMeta columnMeta = map.get(field.getName());
                if(columnMeta == null){
                    throw new DaoException(String.format(ERROR_FORMAT,field,"找不到字段对应的ColumnMeta，当前所有能使用的名称为:"+StringUtils.join(map.keySet(),",")));
                }
                columnName = columnMeta.getName();
                //计算caster
                selectColumn = columnMeta.getName();
                isPrimaryKey = columnMeta.isPrimary();
                isAuto = columnMeta.isAuto();
                entityField = new BeanEntityField(columnName, selectColumn, field);

                //////// //////// //////// //////// ////////
                fillAdapter(entityField, column, columnMeta, dao, field);
            }

            entityAdapterList.add(entityField);
            AutoField autoField = checkAutoField(field, isAuto, entityField);
            entityField.setAutoField(autoField);
            if (autoField != null) {
                autoFields.add(autoField);
                autoRelatedEntityFields.add(entityField);
            }


            if( isPrimaryKey ){
                primaryKeys[primaryKeyIndex++] = entityField;
            }

            ++index;
        }


        if(primaryKeys.length == 0){
            throw new DaoException("绑定实体类"+type+"至少需要定义一个主键");
        }

        AutoEntity autoEntity = createAutoEntity(autoFields, autoRelatedEntityFields);

        return new BeanEntity(
                tableName,
                entityAdapterList.toArray(new EntityField[entityAdapterList.size()]),
                primaryKeys,
                autoEntity,
                type);
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
     * @param isAuto      数据库中是否标注了auto
     * @param entityField
     */
    private AutoField checkAutoField(Field field, boolean isAuto, BeanEntityField entityField) {
        if (isAuto) {
            //如果已经标注了auto，完全又数据库自己处理，只要指定generated key就好了

            return new DatabaseAutoGenerateKey();
        }

        //如果数据库没有标注auto，那么由用户来处理
        AutoGenerate autoGenerate = field.getAnnotation(AutoGenerate.class);
        if (autoGenerate != null) {

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
            entityField.set(entity, value);
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

    private void checkUniqueField(Field field, boolean isUnique, BeanEntity entityField) {

    }

    private void checkPrimaryKeyField() {

    }

    /// rs = ps.getGeneratedKeys();
    //                if(rs.next()){
    //                    autoField.set(data, autoField.getFieldValue(rs.getObject(1)) );
    //                }else{
    //                    log.error("自动生成的字段没有生成成功"+entity.getTable());
    //                }

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

    private String parseColumn(String column,int index){

        Matcher matcher;
        if ((matcher = BuilderKit.AS_PATTERN.matcher(column)).matches()) {
            //直接用原始的
            return column;
        }else{
            //用自动命名,不管是什么都是用AS
            return String.format("%s AS __F%d",column,index);
        }
    }
}
