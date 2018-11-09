package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.*;

import java.lang.reflect.Field;

public abstract class ZoomIocField implements IocField {
    protected IocKey key;

    @Override
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    protected Field field;
    protected IocValue value;

    public ZoomIocField(IocKey key, Field field,IocValue value) {
        field.setAccessible(true);
        this.key = key;
        this.field = field;
        this.value = value;
    }

    @Override
    public IocKey getKey() {
        return key;
    }

    @Override
    public IocValue getValue() {
        return value;
    }


}
