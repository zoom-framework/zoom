package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

public abstract class ZoomIocClass extends IocBase implements IocClass {


    protected IocConstructor constructor;

    protected IocClassLoader classLoader;

    protected IocKey key;

    IocMethod[] methods;
    IocField[] fields;

    IocScope scope;

    int order;

    public ZoomIocClass(
            IocContainer ioc,
            IocScope scope,
            IocClassLoader classLoader,
            IocConstructor constructor,
            IocKey key,
            int order) {
        super(ioc);
        this.classLoader = classLoader;
        this.constructor = constructor;
        this.key = key;
        this.scope =scope;
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public IocScope getScope() {
        return scope;
    }

    @Override
    public IocConstructor getIocConstructor() {
        return constructor;
    }


    @Override
    public IocObject[] getValues(IocKey[] keys) {
        IocObject[] values = new IocObject[keys.length];
        for (int i = 0, c = keys.length; i < c; ++i) {
            values[i] = scope.get(keys[i]);
            if (values[i] == null) {
                IocClass iocClass = classLoader.get(keys[i]);
                values[i] = iocClass.newInstance();
            }
        }
        return values;
    }

    public IocObject fetch(IocConstructor iocConstructor) {
        IocObject obj = scope.get(iocConstructor.getKey());
        if (obj == null) {
            obj = iocConstructor.newInstance();
            scope.put(key, obj);
        }
        return obj;
    }


    @Override
    public IocField[] getIocFields() {
        return fields;
    }

    @Override
    public IocMethod[] getIocMethods() {
        return methods;
    }

    @Override
    public IocClassLoader getIocClassLoader() {
        return classLoader;
    }

    @Override
    public IocKey getKey() {
        return key;
    }

}
