package org.zoomdev.zoom.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * 方法代理
 */
public interface IocMethodProxy {


    /**
     * 获取到一个可以直接调用的IocMethod,可能会取不到
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
     * 使用内部{@link IocMethod}（如果不存在则会创建),执行原始Method
     * @param obj
     * @return
     */
    Object invoke(IocObject obj);

    /**
     * 等同 {@link Method#getAnnotation(Class)}
     * @param annotationClass
     * @param <T>
     * @return
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);
}
