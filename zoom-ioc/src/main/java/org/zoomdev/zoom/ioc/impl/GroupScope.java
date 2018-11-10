package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.ioc.*;

import java.util.Map;

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
        for (Map.Entry<?,IocObject> entry : pool.entrySet()) {
            IocObject value = entry.getValue();
            if(value.get() instanceof IocContainer){
                continue;
            }
            listener.onObjectDestroy(this,value);
            if(value instanceof Destroyable) {
                ((Destroyable)value).destroy();
            }
        }
        parent = null;
    }
}
