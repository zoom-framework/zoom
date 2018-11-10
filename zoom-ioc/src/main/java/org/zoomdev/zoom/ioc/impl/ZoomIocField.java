package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;
import org.zoomdev.zoom.ioc.IocField;
import org.zoomdev.zoom.ioc.IocKey;
import org.zoomdev.zoom.ioc.IocValue;

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
    public String toString() {
        return field.toString();
    }

    @Override
    public IocValue getValue() {
        return value;
    }


}
