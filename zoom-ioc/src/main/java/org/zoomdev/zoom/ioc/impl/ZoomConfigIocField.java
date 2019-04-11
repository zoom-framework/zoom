package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.http.annotations.IocBean;
import org.zoomdev.zoom.http.caster.Caster;
import org.zoomdev.zoom.http.config.ConfigReader;
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
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return IocBean.CONFIG-10;
    }
}
