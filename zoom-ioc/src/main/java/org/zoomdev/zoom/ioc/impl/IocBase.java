package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.http.Destroyable;
import org.zoomdev.zoom.ioc.IocContainer;

public class IocBase implements Destroyable {

    protected IocContainer ioc;

    public IocBase(IocContainer ioc) {
        this.ioc = ioc;
    }

    @Override
    public void destroy() {
        ioc = null;
    }


    public IocContainer getIoc() {
        return ioc;
    }
}
