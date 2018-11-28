package org.zoomdev.zoom.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * IOC方法接口
 */
public interface IocMethod extends IocInjector {

    /**
     * 参宿对应的key
     * @return
     */
    IocKey[] getParameterKeys();

    /**
     * 本方法可以直接自动识别依赖并执行方法
     * @param obj
     * @return
     */
    Object invoke(IocObject obj);

    /**
     * 原始方法
     * @return
     */
    Method getMethod();


    <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass);

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    String getUid();
}
