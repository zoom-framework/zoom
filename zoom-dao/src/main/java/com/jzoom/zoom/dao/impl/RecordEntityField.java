package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.common.utils.DataObject;

public class RecordEntityField extends AbstractEntityField {


    private String field;


    RecordEntityField(String column, String select,String field) {
        super(column, select);
        this.field = field;
    }

    @Override
    public Object getFieldValue(Object columnValue) {
        if (caster != null) {
            return caster.to(columnValue);
        }
        return columnValue;
    }

    @Override
    public Object get(Object target) {
        return ((DataObject)target).get(field);
    }

    @Override
    public String getFieldName() {
        return field;
    }

    @Override
    public void set(Object data, Object fieldValue) {
        ((DataObject)data).put(field,fieldValue);
    }
}
