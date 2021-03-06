package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.config.ConfigReader;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocObject;

import java.lang.reflect.Field;

public class ZoomConfigIocField extends ZoomIocField {

    private String config;

    public ZoomConfigIocField(IocContainer ioc, Field field, String name) {
        super(ioc, field);
        this.config = name;
    }

    @Override
    public void set(IocObject obj) {
        try {
            field.set(obj.get(), Caster.toType(ConfigReader.getDefault().get(config), field.getGenericType()));
        } catch (IllegalAccessException e) {
            throw new ZoomException(e);
        }
    }

    @Override
    public int getOrder() {
        return IocBean.CONFIG - 10;
    }
}
