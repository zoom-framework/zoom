package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocField;
import org.zoomdev.zoom.ioc.IocObject;

import java.lang.reflect.Field;

public abstract class ZoomIocField extends IocBase implements IocField {

    @Override
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    protected Field field;



    public ZoomIocField(IocContainer ioc,Field field){
        super(ioc);
        field.setAccessible(true);
        this.field = field;
    }


    @Override
    public void inject(IocObject target) {
        set(target);
    }
}
