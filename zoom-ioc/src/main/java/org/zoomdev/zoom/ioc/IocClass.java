package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;

/**
 * 用于描述Ioc容器内部对象的创建信息
 */
public interface IocClass {


    IocObject newInstance(IocScope scope);

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


    IocMethod getMethod(Method method);


}
