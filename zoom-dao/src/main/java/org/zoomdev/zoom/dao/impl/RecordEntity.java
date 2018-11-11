package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.NameAdapter;

public class RecordEntity extends AbstractEntity {
    RecordEntity(String table,
                 EntityField[] entityFields,
                 EntityField[] primaryKeys,
                 AutoEntity autoEntity,
                 NameAdapter nameAdapter) {
        super(table, entityFields, primaryKeys, autoEntity,nameAdapter);
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
