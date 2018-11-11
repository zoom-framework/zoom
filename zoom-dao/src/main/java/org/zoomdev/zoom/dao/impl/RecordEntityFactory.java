package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.dao.AutoField;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Entity getEntity( Class<?> type, String tableName) {
        TableMeta tableMeta = getTableMeta(tableName);

        //获取到field和column的对应关系
        final List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>(tableMeta.getColumns().length);

        RenameUtils.rename(dao, tableMeta, new RenameUtils.ColumnRenameVisitor() {
            @Override
            public void visit(TableMeta tableMeta, ColumnMeta columnMeta, String fieldName,String selectColumnName) {
                RecordEntityField entityField = new RecordEntityField(
                         fieldName
                );
                //单表的情况下无须有as
                entityField.setSelectColumnName(columnMeta.getName());
                entityField.setColumn(columnMeta.getName());
                //只有clob blob 需要适配，
                entityField.setCaster(getCaster(columnMeta.getDataType()));
                //
                entityField.setStatementAdapter(dao.getStatementAdapter(columnMeta.getDataType()));

                entityFields.add(entityField);
            }
        });


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
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            if (columnMeta.isAuto()) {
                AutoField autoField = new BeanEntityFactory.DatabaseAutoGenerateKey();
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
        }

        return null;
    }

    @Override
    public Entity getEntity(Class<?> type, String[] tables) {
        // 得到一个映射关系
        final List<RecordEntityField> entityFields = new ArrayList<RecordEntityField>();

        RenameUtils.rename(dao, tables, new RenameUtils.ColumnRenameVisitor() {
            @Override
            public void visit(TableMeta tableMeta, ColumnMeta columnMeta, String fieldName, String selectColumnName) {
                RecordEntityField entityField = new RecordEntityField(fieldName);
                entityField.setColumn( tableMeta.getName() + "." + columnMeta.getName());
                entityField.setSelectColumnName(selectColumnName);
                //只有clob blob 需要适配，
                entityField.setCaster(getCaster(columnMeta.getDataType()));
                //
                entityField.setStatementAdapter(dao.getStatementAdapter(columnMeta.getDataType()));

                entityFields.add(entityField);
            }
        });

        return new RecordEntity(
                tables[0],
                entityFields.toArray(new EntityField[entityFields.size()]),
                null,
                null,
                null);
    }
}
