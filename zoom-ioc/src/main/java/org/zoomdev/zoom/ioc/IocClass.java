package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;

/**
 * 用于描述Ioc容器内部对象的创建信息
 */
public interface IocClass {


    IocScope getScope();


    IocObject newInstance();

    /**
     * 与IocContainer的getValues不同，获取到的IocObject为未注入初始化的对象
     *
     * @param keys
     * @return
     */
    IocObject[] getValues(IocKey[] keys);

    IocConstructor getIocConstructor();

    IocField[] getIocFields();

    /**
     * 获取method
     *
     * @return
     */
    IocMethod[] getIocMethods();

    /**
     * 类似Class.getClassLoader
     *
     * @return
     */
    IocClassLoader getIocClassLoader();

    /**
     * Ioc容器对象标志
     *
     * @return
     */
    IocKey getKey();


    IocMethod getIocMethod(Method method);


    int getOrder();

    IocContainer getIoc();


}
