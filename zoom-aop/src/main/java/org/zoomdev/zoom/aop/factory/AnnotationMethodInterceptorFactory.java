package org.zoomdev.zoom.aop.factory;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInterceptorFactory;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.utils.Classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public abstract class AnnotationMethodInterceptorFactory<T extends Annotation> implements MethodInterceptorFactory, Destroyable {


    private Class<T> annotationClass;


    @SuppressWarnings("unchecked")
    public AnnotationMethodInterceptorFactory() {
        //获取泛型类型
        annotationClass = (Class<T>) Classes.getAllParameterizedTypes(getClass())[0];
    }

    @Override
    public void destroy() {
        annotationClass = null;
    }

    @Override
    public void createMethodInterceptors(Class<?> tartetClass, Method method, List<MethodInterceptor> interceptors) {
        int modifier = method.getModifiers();
        if (Modifier.isFinal(modifier) || Modifier.isPrivate(modifier)) {
            return;
        }
        T annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            createMethodInterceptors(annotation, method, interceptors);
        }
    }

    protected abstract void createMethodInterceptors(T annotation, Method method, List<MethodInterceptor> interceptors);


}
