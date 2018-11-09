package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.caster.ValueCaster;
import com.jzoom.zoom.dao.AutoField;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.dao.Entity;
import com.jzoom.zoom.dao.EntityFactory;
import com.jzoom.zoom.dao.adapters.EntityField;
import com.jzoom.zoom.dao.alias.AliasPolicy;
import com.jzoom.zoom.dao.alias.AliasPolicyMaker;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.TableMeta;

import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;

public class RecordEntityFactory implements EntityFactory {

    private String[] getColumnNames(TableMeta meta) {

        String[] names = new String[meta.getColumns().length];
        int index = 0;
        for (ColumnMeta columnMeta : meta.getColumns()) {
            names[index++] = columnMeta.getName();
        }
        return names;
    }

    private AliasPolicyMaker maker;

    public RecordEntityFactory(AliasPolicyMaker maker){
        this.maker = maker;
    }

    @Override
    public Entity getEntity(Dao dao, Class<?> type) {
        return null;
    }


    private ValueCaster getCaster(Class<?> columnType){
        if(columnType == Clob.class){
            return Caster.wrap(columnType,String.class);
        }
        //byte array
        if(columnType == Blob.class){
            return Caster.wrap(columnType,byte[].class);
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

    @Override
    public Entity getEntity(Dao dao, Class<?> type, String tableName) {
        DbStructFactory dbStructFactory = dao.getDbStructFactory();
        TableMeta tableMeta = dbStructFactory.getTableMeta(dao.ar(), tableName);
        dbStructFactory.fill(dao.ar(), tableMeta);
        //获取到field和column的对应关系
        AliasPolicy aliasPolicy = maker.getAliasPolicy(getColumnNames(tableMeta));
        List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>(tableMeta.getColumns().length);

        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            String field = aliasPolicy.getAlias(columnMeta.getName());
            RecordEntityField entityField =  new RecordEntityField(
                    columnMeta.getName(),columnMeta.getName(),field
            );
            //只有clob blob 需要适配，
            entityField.setCaster(getCaster(columnMeta.getDataType()));
            //
            entityField.setStatementAdapter( dao.getStatementAdapter(columnMeta.getDataType()));

            entityFields.add(entityField);
        }

        return new RecordEntity(
                tableName,
                entityFields.toArray(new EntityField[entityFields.size()]),
                getPrimaryKeys(tableMeta, entityFields),
                createAutoEntity(tableMeta, entityFields));
    }

    private AutoEntity createAutoEntity(TableMeta tableMeta, List<RecordEntityField> entityFields) {
        //如果是数据库的自动生成值
        int index = 0;
        List<String> generatedKeys = new ArrayList<String>();
        List<EntityField> generatedEntityFields = new ArrayList<EntityField>();
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            if (columnMeta.isAuto()) {
                AutoField autoField = new BeanEntityFactory.DatabaseAutoGenerateKey();
                RecordEntityField entityField = entityFields.get(index);
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
        }

        return null;
    }

    @Override
    public Entity getEntity(Dao dao, Class<?> type, String[] tables) {
        return null;
    }
}
