package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractEntity implements Entity {
    EntityField[] entityFields;
    EntityField[] primaryKeys;
    String table;

    AutoEntity autoEntity;

    private Map<String, String> file2column;

    private Map<String, String> namesMap;

    AbstractEntity(
            String table,
            EntityField[] entityFields,
            EntityField[] primaryKeys,
            AutoEntity autoEntity,
            Map<String, String> namesMap) {
        this.table = table;
        this.entityFields = entityFields;
        this.primaryKeys = primaryKeys;
        this.autoEntity = autoEntity;
        this.file2column = new ConcurrentHashMap<String, String>();
        this.namesMap = namesMap;
    }


    @Override
    public EntityField[] getEntityFields() {
        return entityFields;
    }

    @Override
    public EntityField[] getPrimaryKeys() {
        return primaryKeys;
    }


    @Override
    public String getTable() {
        return table;
    }


    @Override
    public PreparedStatement prepareInsert(Connection connection, String sql) throws SQLException {
        // generate keys or next_val
        //"insert into xx values ()"
        if (autoEntity != null) {
            return autoEntity.prepareInsert(connection, sql);
        }
        return connection.prepareStatement(sql);
    }

    @Override
    public void afterInsert(Object data, PreparedStatement ps) throws SQLException {
        if (autoEntity != null) {
            autoEntity.afterInsert(data, ps);
        }
    }

    @Override
    public int getFieldCount() {
        return entityFields.length;
    }


    @Override
    public String getColumnName(String field) {
        if (field == null) {
            throw new NullPointerException("字段名称为空");
        }
        String column = file2column.get(field);
        if (column == null) {
            for (EntityField entityField : entityFields) {
                if (entityField.getFieldName().equals(field)) {
                    column = entityField.getColumnName();
                    file2column.put(field, column);
                    break;
                }
            }
            if (column == null && namesMap != null) {
                column = namesMap.get(field);
                if (column != null) {
                    file2column.put(field, column);
                }
            }
        }
        if (column == null) {
            throw new DaoException(
                    String.format("找不到字段%s对应的列名称,所有可能的字段列表为" + getAvaliableFields(),
                            field));
        }
        return column;
    }


    private String getAvaliableFields() {
        Set<String> list = new LinkedHashSet<String>();

        for (EntityField entityField : entityFields) {
            list.add(entityField.getFieldName());
        }

        list.addAll(namesMap.keySet());


        return StringUtils.join(list, ",");
    }

    @Override
    public void validate(Object data) {
        for(EntityField field : entityFields){
            field.validate(field.get(data));
        }
    }
}
