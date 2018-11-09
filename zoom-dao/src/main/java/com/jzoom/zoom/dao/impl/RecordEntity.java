package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.adapters.EntityField;

public class RecordEntity extends AbstractEntity {
    RecordEntity(String table,
                 EntityField[] entityFields,
                 EntityField[] primaryKeys,
                 AutoEntity autoEntity) {
        super(table, entityFields, primaryKeys, autoEntity);
    }

    @Override
    public Class<?> getType() {
        return Record.class;
    }

    @Override
    public Object newInstance() {
        return new Record();
    }
}
