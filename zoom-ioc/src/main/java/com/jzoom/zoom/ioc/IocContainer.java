package com.jzoom.zoom.ioc;

import com.jzoom.zoom.common.Destroyable;

import java.lang.reflect.Method;

public interface IocContainer extends Destroyable {

    /**
     * ioc对象的范围
     */
    enum Scope {
        REQUEST,
        SESSION,
        APPLICATION
    }


    /**
     * 通过name、class找到一个Ioc容器对象
     *
     * @param key
     * @return
     */
    IocObject get(IocKey key);


    /**
     * 直接通过class找到ioc容器对象
     *
     * @param type
     * @param <T>
     * @return
     */
    <T> T get(Class<?> type);


    /**
     * IOC容器类加载器
     *
     * @return
     */
    IocClassLoader getIocClassLoader();


    /**
     * 设置类增强
     *
     * @param enhancer
     */
    void setClassEnhancer(ClassEnhancer enhancer);

    /**
     * @param listener
     */
    void addEventListener(IocEventListener listener);


    /**
     * @param obj
     * @param iocMethod
     * @return
     */
    Object invokeMethod(IocObject obj, IocMethod iocMethod);

    /**
     * @param iocClass
     * @param target
     * @return
     */
    IocMethodProxy getMethodProxy(IocClass iocClass, Method target);


    /**
     * 释放指定范围的对象
     *
     * @param scope
     */
    void release(Scope scope);
}
