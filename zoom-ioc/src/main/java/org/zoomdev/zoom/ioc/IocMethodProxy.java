package org.zoomdev.zoom.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface IocMethodProxy {


    /**
     * 有可能没有
     *
     * @return
     */
    IocMethod getIocMethod();

    /**
     * 原方法
     *
     * @return
     */
    Method getMethod();


    /**
     * 唯一id
     *
     * @return
     */
    String getUid();


    /**
     * @param obj
     * @return
     */
    Object invoke(IocObject obj);

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);
}
