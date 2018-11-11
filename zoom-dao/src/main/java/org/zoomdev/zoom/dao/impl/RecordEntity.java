package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.EAr;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.NameAdapter;

import java.util.Map;

public class RecordEntity extends AbstractEntity {
    RecordEntity(String table,
                 EntityField[] entityFields,
                 EntityField[] primaryKeys,
                 AutoEntity autoEntity,
                 Map<String,String> namesMap) {
        super(table, entityFields, primaryKeys, autoEntity,namesMap);
    }

    @Override
    public Class<?> getType() {
        return Record.class;
    }

    @Override
    public Object newInstance() {
        return new Record();
    }

    @Override
    public void setSource(SqlBuilder builder) {
        builder.table("");
    }

}
