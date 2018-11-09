package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractEntity implements Entity {
    EntityField[] entityFields;
    EntityField[] primaryKeys;
    String table;

    AutoEntity autoEntity;

    AbstractEntity(String table,
               EntityField[] entityFields,
               EntityField[] primaryKeys,
               AutoEntity autoEntity) {
        this.table = table;
        this.entityFields = entityFields;
        this.primaryKeys = primaryKeys;
        this.autoEntity = autoEntity;
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
}
