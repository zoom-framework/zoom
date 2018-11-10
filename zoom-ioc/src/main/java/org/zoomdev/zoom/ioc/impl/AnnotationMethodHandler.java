package org.zoomdev.zoom.ioc.impl;


import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.IocMethodHandler;
import org.zoomdev.zoom.ioc.IocMethodProxy;
import org.zoomdev.zoom.ioc.IocObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AnnotationMethodHandler<T extends Annotation> implements IocMethodHandler {

    private Class<T> annotationClass;
    @SuppressWarnings("unchecked")
    public AnnotationMethodHandler() {
        //获取泛型类型
        annotationClass = (Class<T>) Classes.getAllParameterizedTypes(getClass())[0];
    }
    @Override
    public void destroy(IocObject target, Method method) {
        T annotation = method.getAnnotation(annotationClass);
        destroy(target,annotation,method);
    }
    protected void destroy(IocObject target,T annotation, Method method) {

    }
    @Override
    public void create(IocObject target, Method method, IocMethodProxy proxy) {
       T annotation = method.getAnnotation(annotationClass);
        visit(target,annotation,method,proxy);
    }

    @Override
    public boolean accept(Method method) {
        return method.isAnnotationPresent(annotationClass);
    }

    protected abstract void visit(IocObject target, T annotation, Method method, IocMethodProxy proxy);
}
