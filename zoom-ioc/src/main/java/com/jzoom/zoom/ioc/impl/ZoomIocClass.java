package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.*;

public abstract class ZoomIocClass implements IocClass {


    protected IocConstructor constructor;

    protected IocClassLoader classLoader;

    protected IocKey key;

    IocMethod[] methods;
    IocField[] fields;


    public ZoomIocClass(IocClassLoader classLoader, IocConstructor constructor, IocKey key) {
        this.classLoader = classLoader;
        this.constructor = constructor;
        this.key = key;
    }


	protected IocObject[] getValues(IocScope scope, IocKey[] keys) {
		IocObject[] values = new IocObject[keys.length];
		for (int i = 0, c = keys.length; i < c; ++i) {
			values[i] = scope.get(keys[i]);
			if (values[i] == null) {
				IocClass iocClass = classLoader.get(keys[i]);
				values[i] = iocClass.newInstance(scope);
			}
		}
		return values;
	}
	
	public IocObject getAndCreate(IocScope scope, IocConstructor iocConstructor) {
		IocObject obj = scope.get(iocConstructor.getKey());
		if (obj == null) {
			IocKey key = iocConstructor.getKey();
			IocKey[] keys = iocConstructor.getParameterKeys();
			obj = iocConstructor.newInstance(getValues(scope, keys));
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
