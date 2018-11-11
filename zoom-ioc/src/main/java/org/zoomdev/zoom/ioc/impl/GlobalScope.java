package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.ioc.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalScope implements IocScope,Destroyable {

	protected Map<IocKey, IocObject> pool = new ConcurrentHashMap<IocKey, IocObject>();

    protected IocEventListener listener;

    public GlobalScope(IocContainer ioc, IocEventListener listener) {
        this.ioc = ioc;
        this.listener = listener;
    }


    private IocContainer ioc;

	@Override
	public IocObject put(IocKey key, IocObject value) {
		pool.put(key, value);
        listener.onObjectCreated(this, value);
		return value;
	}

    @Override
    public IocContainer getIoc() {
        return ioc;
    }

    @Override
	public IocObject get(IocKey key) {
		return pool.get(key);
	}

	@Override
	public void destroy() {

        for (Map.Entry<?,IocObject> entry : pool.entrySet()) {
            IocObject value = entry.getValue();
            if(value.get() instanceof IocContainer){
                continue;
            }
            if(value instanceof Destroyable) {
                ((Destroyable)value).destroy();
            }
        }

	}
}
