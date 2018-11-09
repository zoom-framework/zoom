package com.jzoom.zoom.ioc.impl;


import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.ioc.IocMethodHandler;
import com.jzoom.zoom.ioc.IocMethodProxy;
import com.jzoom.zoom.ioc.IocObject;

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
    public void visit(IocObject target, Method method, IocMethodProxy proxy) {
       T annotation = method.getAnnotation(annotationClass);
        visit(target,annotation,method,proxy);
    }

    @Override
    public boolean accept(Method method) {
        return method.isAnnotationPresent(annotationClass);
    }

    protected abstract void visit(IocObject target, T annotation, Method method, IocMethodProxy proxy);
}
