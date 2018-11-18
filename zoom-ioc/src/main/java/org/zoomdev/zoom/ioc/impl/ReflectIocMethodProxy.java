package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.IocMethod;
import org.zoomdev.zoom.ioc.IocMethodProxy;
import org.zoomdev.zoom.ioc.IocObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ReflectIocMethodProxy implements IocMethodProxy {

    public ReflectIocMethodProxy(Method method) {
        this.method = method;
    }

    private Method method;


    @Override
    public IocMethod getIocMethod() {
        return null;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String getUid() {
        return method.toString();
    }

    @Override
    public Object invoke(IocObject obj) {

        return null;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }
}
