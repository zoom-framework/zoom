package org.zoomdev.zoom.ioc;

import org.zoomdev.zoom.common.annotations.IocBean;

import java.lang.reflect.Method;
import java.util.List;

public interface IocClassLoader {

    IocClass get(IocKey key);

    /**
     * 增加一个IocBean方法配置
     *
     * @param moduleInstance module实例
     * @param method         module的方法
     * @param order         {@link IocBean#order()}
     */
    IocClass append(Object moduleInstance, Method method,int order);

    /**
     * 注册增加一个实例
     *
     * @param baseType
     * @param instance
     * @param initialized
     * @param order {@link IocBean#SYSTEM} {@link IocBean#USER}
     * @return
     */
    <T> IocClass append(Class<T> baseType, T instance, boolean initialized,int order);


    /**
     * 直接增加一个实际类
     *
     * @param type
     */
    IocClass append(Class<?> type);


    void setClassEnhancer(ClassEnhancer enhancer);

    List<IocClass> appendModule(Class<?> moduleClass);

    ClassLoader getClassLoader();
}
