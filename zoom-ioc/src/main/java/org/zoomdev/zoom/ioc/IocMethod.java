package org.zoomdev.zoom.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * IOC方法接口
 */
public interface IocMethod extends IocInjector {

    IocKey[] getParameterKeys();

    Object invoke(IocObject obj);

    Method getMethod();


    <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass);

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    String getUid();
}
