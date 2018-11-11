package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.AutoField;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.EntityFactory;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyMaker;
import org.zoomdev.zoom.dao.alias.impl.CamelAliasPolicy;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public RecordEntityFactory(AliasPolicyMaker maker) {
        this.maker = maker;
    }

    @Override
    public Entity getEntity(Dao dao, Class<?> type) {
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

    @Override
    public Entity getEntity(Dao dao, Class<?> type, String tableName) {
        DbStructFactory dbStructFactory = dao.getDbStructFactory();
        TableMeta tableMeta = dbStructFactory.getTableMeta(tableName);
        dbStructFactory.fill(tableMeta);
        //获取到field和column的对应关系
        AliasPolicy aliasPolicy = maker.getAliasPolicy(getColumnNames(tableMeta));
        List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>(tableMeta.getColumns().length);

        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            String field = aliasPolicy.getAlias(columnMeta.getName());
            RecordEntityField entityField = new RecordEntityField(
                    columnMeta.getName(), columnMeta.getName(), field
            );
            //只有clob blob 需要适配，
            entityField.setCaster(getCaster(columnMeta.getDataType()));
            //
            entityField.setStatementAdapter(dao.getStatementAdapter(columnMeta.getDataType()));

            entityFields.add(entityField);
        }

        return new RecordEntity(
                tableName,
                entityFields.toArray(new EntityField[entityFields.size()]),
                getPrimaryKeys(tableMeta, entityFields),
                createAutoEntity(tableMeta, entityFields),
                null);
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
        AliasPolicy tableAliasPolicy = maker.getAliasPolicy(tables);
        // 得到一个映射关系
        List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>();

        boolean first = true;
        for (String table : tables) {
            TableMeta meta = dao.getDbStructFactory().getTableMeta(table);
            String tableAlia = tableAliasPolicy.getAlias(table);
            // 取出每一个表的重命名策略
            AliasPolicy columnAlias = maker.getAliasPolicy(getColumnNames(meta));
            if (columnAlias == null) {
                columnAlias = CamelAliasPolicy.DEFAULT;
            }
            for (ColumnMeta columnMeta : meta.getColumns()) {
                String alias = columnAlias.getAlias(columnMeta.getName());
                //如果是第一个表，则直接使用字段名称，否则使用table.column的形式
                String fieldName = first ? alias : (tableAlia + StrKit.upperCaseFirst(alias));
                String underLineName = StrKit.toUnderLine(fieldName);
                String asColumnName = table + "." + dao.getDriver().protectColumn(columnMeta.getName())
                        + " AS "
                        + dao.getDriver().protectColumn(underLineName + "_");

                RecordEntityField entityField = new RecordEntityField(
                        table + "." + columnMeta.getName(), asColumnName, fieldName
                );
                //只有clob blob 需要适配，
                entityField.setCaster(getCaster(columnMeta.getDataType()));
                //
                entityField.setStatementAdapter(dao.getStatementAdapter(columnMeta.getDataType()));

                entityFields.add(entityField);
            }
            if (first) {
                first = false;
            }
        }

        return new RecordEntity(
                tables[0],
                entityFields.toArray(new EntityField[entityFields.size()]),
                null,
                null,
                null);
    }
}
