package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

public class GroupScope extends GlobalScope implements IocScope {

    private IocScope parent;

    public GroupScope(IocContainer ioc, IocEventListener listener,IocScope parent) {
        super(ioc, listener);
        this.parent = parent;
    }


    @Override
    public IocObject get(IocKey key) {
        IocObject value = super.get(key);
        if(value==null){
            value = parent.get(key);
        }
        return value;
    }

    @Override
    public void destroy() {
        super.destroy();
        parent = null;
    }
}
