package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.DaoException;

import java.lang.reflect.Field;

class BeanEntityField extends AbstractEntityField {

    private Field field;


    BeanEntityField(
            String column, String select, Field field) {
        super(column, select);
        this.field = field;
    }

    @Override
    public Object get(Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new DaoException("设置字段的值发生错误"+field,e);
        }
    }



    @Override
    public String getFieldName() {
        return field.getName();
    }


    @Override
    public void set(Object data, Object fieldValue) {
        try {
            field.set(data,fieldValue);
        } catch (IllegalAccessException e) {
            throw new DaoException("设置值出错"+field+" value:"+fieldValue,e);
        }
    }


}
