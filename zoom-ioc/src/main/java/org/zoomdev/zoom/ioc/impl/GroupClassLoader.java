package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.IocClass;
import org.zoomdev.zoom.ioc.IocClassLoader;
import org.zoomdev.zoom.ioc.IocKey;

public class GroupClassLoader extends ZoomIocClassLoader {

    private IocClassLoader parent;

    GroupClassLoader(IocClassLoader parent){
        this.parent = parent;
    }

    @Override
    public IocClass get(IocKey key) {
        IocClass iocClass = super.get(key);
        if(iocClass == null){
            iocClass = this.parent.get(key);
        }
        return iocClass;
    }
}
