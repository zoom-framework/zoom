package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.utils.DataObject;

import java.lang.reflect.Type;


public class RecordEntityField extends AbstractEntityField {


    private String field;

    private Class<?> type;


    RecordEntityField(String field,Class<?> type) {
        super();
        this.field = field;
        this.type = type;
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
        return ((DataObject) target).get(field);
    }

    @Override
    public String getFieldName() {
        return field;
    }

    @Override
    public void set(Object data, Object fieldValue) {
        ((DataObject) data).put(field, fieldValue);
    }

    /**
     * 如果是Record基本上就是数据库类型了
     * @return
     */
    @Override
    public Type getFieldType() {
        return type;
    }


}
