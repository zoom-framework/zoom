package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.dao.EntityFactory;
import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.auto.DatabaseAutoGenerateKey;
import org.zoomdev.zoom.dao.driver.AutoGenerateProvider;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.utils.DaoUtils;

import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordEntityFactory extends AbstractEntityFactory {


    public RecordEntityFactory(Dao dao) {
        super(dao);
    }

    @Override
    public Entity getEntity(Class<?> type) {
        return null;
    }


    private ValueCaster getCaster(Class<?> columnType) {
        if (columnType == Clob.class) {
            return Caster.wrap(columnType, String.class);
        }
        //byte array
        if (columnType == Blob.class) {
            return Caster.wrap(columnType, byte[].class);
        }

        // 不需要
        return null;
    }


    private EntityField[] getPrimaryKeys(TableMeta tableMeta, List<RecordEntityField> entityFields) {
        EntityField[] primaryKeys = new EntityField[tableMeta.getPrimaryKeys().length];
        int index = 0;
        int i = 0;
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            if (columnMeta.isPrimary()) {
                primaryKeys[i++] = entityFields.get(index);
            }
            ++index;
        }
        return primaryKeys;
    }


    public Entity getEntityOne(Class<?> type, String tableName) {

        //获取到field和column的对应关系
        final List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>();

        Map<String,RenameUtils.ColumnRenameConfig> map =  RenameUtils.rename(dao, tableName);
        RenameUtils.ColumnRenameConfig firstTable = map.values().iterator().next();
        TableMeta tableMeta = firstTable.tableMeta;

        for (Map.Entry<String,RenameUtils.ColumnRenameConfig> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            RenameUtils.ColumnRenameConfig config = entry.getValue();
            ColumnMeta columnMeta = config.columnMeta;
            tableMeta = config.tableMeta;
            String selectColumnName = config.selectColumnName;

            RecordEntityField entityField = new RecordEntityField(
                    fieldName,
                    DaoUtils.normalizeType(columnMeta.getDataType())
            );
            //单表的情况下无须有as
            entityField.setSelectColumnName(columnMeta.getName());
            entityField.setColumn(columnMeta.getName());
            //只有clob blob 需要适配，
            entityField.setCaster(getCaster(columnMeta.getDataType()));
            //
            entityField.setStatementAdapter(dao.getStatementAdapter(columnMeta.getDataType()));
            entityField.setOriginalFieldName(columnMeta.getName());
            entityField.setColumnMeta(columnMeta);
            entityFields.add(entityField);
        }


        return new RecordEntity(
                tableName,
                entityFields.toArray(new EntityField[entityFields.size()]),
                getPrimaryKeys(tableMeta, entityFields),
                createAutoEntity(tableMeta, entityFields),
                null);
    }


    protected AutoEntity createAutoEntity(TableMeta tableMeta, List<? extends AbstractEntityField> entityFields) {
        //如果是数据库的自动生成值
        int index = 0;
        List<String> generatedKeys = new ArrayList<String>();
        List<EntityField> generatedEntityFields = new ArrayList<EntityField>();

        List<AbstractEntityField> primaryKeys = new ArrayList<AbstractEntityField>();

        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            AutoField autoField = null;
            if (columnMeta.isAuto()) {
                autoField = new DatabaseAutoGenerateKey();
            }
            if(columnMeta.isPrimary()){
                primaryKeys.add(entityFields.get(index));
            }
            if(autoField!=null){
                AbstractEntityField entityField = entityFields.get(index);
                entityField.setAutoField(autoField);
                if (autoField.isDatabaseGeneratedKey()) {
                    generatedKeys.add(entityField.getColumnName());
                    generatedEntityFields.add(entityField);
                }
            }
            ++index;
        }


        if (generatedKeys.size() > 0) {
            return new ZoomAutoEntity(
                    generatedKeys.toArray(new String[generatedKeys.size()]),
                    generatedEntityFields.toArray(new EntityField[generatedEntityFields.size()])
            );
        }else{

            if( primaryKeys.size() == 1 ){
                AbstractEntityField pk = primaryKeys.get(0);
                index = entityFields.indexOf(pk);

                if(dao.getDriver() instanceof AutoGenerateProvider){
                    AutoField autoField = ((AutoGenerateProvider)dao.getDriver())
                            .createAutoField( dao, tableMeta,tableMeta.getColumns()[index]);
                    if(autoField!=null){
                        pk.setAutoField(autoField);
                    }
                    return new ZoomAutoEntity(
                            new String[]{pk.getColumnName()},
                            new EntityField[]{pk}
                    );
                }

            }


        }

        return null;
    }


    private RecordEntityField createEntityFieldFromConfig(String fieldName,RenameUtils.ColumnRenameConfig config){
        ColumnMeta columnMeta = config.columnMeta;
        TableMeta tableMeta = config.tableMeta;
        String selectColumnName = config.selectColumnName;

        RecordEntityField entityField = new RecordEntityField(fieldName, DaoUtils.normalizeType(columnMeta.getDataType()));
        entityField.setColumn(tableMeta.getName() + "." + columnMeta.getName());
        entityField.setSelectColumnName(selectColumnName);
        //只有clob blob 需要适配，
        entityField.setCaster(getCaster(columnMeta.getDataType()));
        //
        entityField.setStatementAdapter(dao.getStatementAdapter(columnMeta.getDataType()));
        entityField.setColumnMeta(columnMeta);


        return entityField;
    }

    @Override
    public Entity getEntity(Class<?> type, String... tables) {
        if(tables.length==1){
            return getEntityOne(type,tables[0]);
        }else{
            // 得到一个映射关系
            final List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>();

            Map<String,RenameUtils.ColumnRenameConfig> map = RenameUtils.rename(
                dao,tables
            );

            for (Map.Entry<String,RenameUtils.ColumnRenameConfig> entry : map.entrySet()) {
                String fieldName = entry.getKey();
                RenameUtils.ColumnRenameConfig config = entry.getValue();
                entityFields.add(createEntityFieldFromConfig(fieldName,config));
            }
            return new RecordEntity(
                    tables[0],
                    entityFields.toArray(new EntityField[entityFields.size()]),
                    null,
                    null,
                    null);
        }
    }


}
