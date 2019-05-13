package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.IocCreatorFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class ZoomCreatorFactory implements IocCreatorFactory,InvocationHandler {
    @Override
    public Object create(Class<?> interfaceClass) {
        return Proxy.newProxyInstance(ZoomCreatorFactory.class.getClassLoader(),new Class<?>[]{interfaceClass},this);
    }

    @Override
    public abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
