package org.zoomdev.zoom.ioc;

import org.zoomdev.zoom.common.Destroyable;

import java.lang.reflect.Method;
import java.util.List;

public interface IocContainer extends Destroyable {

    List<IocEventListener> getEventListeners();

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
     * 如果找不到会报错 {@link IocException}
     *
     * @param key
     * @return
     */
    IocObject fetch(IocKey key);


    /**
     * 直接通过class找到ioc容器对象
     * 如果找不到会报错 {@link IocException}
     *
     * @param type
     * @param <T>
     * @return
     */
    <T> T fetch(Class<?> type);


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
     * @param iocClass
     * @param target
     * @return
     */
    IocMethod getMethod(IocClass iocClass, Method target);


    /**
     * 释放指定范围的对象
     *
     * @param scope
     */
    void release(Scope scope);


    IocScope getScope();
}
