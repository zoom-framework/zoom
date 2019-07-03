package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.config.ConfigReader;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.IocValue;

import java.lang.reflect.Type;

public class ZoomConfigValue implements IocValue {


    private String name;
    private Type type;

    public ZoomConfigValue(String name, Type type) {
        this.name = name;
        this.type = type;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public IocObject getValue(IocContainer ioc) {
        Object v = ConfigReader.getDefault().get(name);
        v = Caster.toType(v, type);
        return ZoomIocObject.wrap(null, v, true);
    }
}
