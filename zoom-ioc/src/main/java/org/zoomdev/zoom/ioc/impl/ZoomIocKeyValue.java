package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.IocKey;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.IocValue;

public class ZoomIocKeyValue implements IocValue {

    private final IocKey key;

    public ZoomIocKeyValue(IocKey key){
        this.key = key;
    }

    @Override
    public IocObject getValue(IocContainer ioc) {
        return ioc.fetch(key);
    }

    @Override
    public String toString() {
        return key.toString();
    }

    public IocKey getKey() {
        return key;
    }
}
