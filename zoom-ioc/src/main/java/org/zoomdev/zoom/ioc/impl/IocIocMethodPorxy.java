package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.IocException;
import org.zoomdev.zoom.ioc.IocMethod;
import org.zoomdev.zoom.ioc.IocMethodProxy;
import org.zoomdev.zoom.ioc.IocObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IocIocMethodPorxy implements IocMethodProxy {

    public IocIocMethodPorxy(IocMethod iocMethod) {
        this.iocMethod = iocMethod;
    }

    private IocMethod iocMethod;

    @Override
    public IocMethod getIocMethod() {
        return iocMethod;
    }

    @Override
    public Method getMethod() {
        return iocMethod.getMethod();
    }

    @Override
    public String getUid() {
        return iocMethod.getUid();
    }

    @Override
    public Object invoke(IocObject obj) {
        return iocMethod.invoke(obj);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return iocMethod.getAnnotation(annotationClass);
    }
}
