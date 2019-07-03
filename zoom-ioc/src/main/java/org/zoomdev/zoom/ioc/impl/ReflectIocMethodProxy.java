package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ReflectIocMethodProxy implements IocMethodProxy {

    private IocContainer iocContainer;
    private IocClass iocClass;

    public ReflectIocMethodProxy(
            IocContainer iocContainer,
            IocClass iocClass,
            Method method) {
        this.method = method;
        this.iocContainer = iocContainer;
        this.iocClass = iocClass;
    }

    private Method method;
    private IocMethod iocMethod;


    @Override
    public IocMethod getIocMethod() {
        if (iocMethod == null) {
            iocMethod = iocContainer.getMethod(
                    iocClass,
                    method
            );
        }
        return iocMethod;
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
        return getIocMethod().invoke(obj);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }
}
