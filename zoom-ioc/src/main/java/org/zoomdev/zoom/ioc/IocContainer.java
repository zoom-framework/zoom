package org.zoomdev.zoom.ioc;

import org.zoomdev.zoom.common.Destroyable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * ioc容器接口
 */
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
     * @param key {@link org.zoomdev.zoom.ioc.impl.ZoomIocKey}
     * @return
     */
    IocObject fetch(IocKey key);


    /**
     * 直接通过class找到ioc容器中的对象
     * 如果找不到会报错 {@link IocException}
     *
     * @param type
     * @param <T>
     * @return
     */
    <T> T fetch(Class<T> type);

    /**
     *
     * 通过key获取ioc容器中的对象，如果找不到则返回null
     * @param key {@link org.zoomdev.zoom.ioc.impl.ZoomIocKey}
     * @return
     */
    IocObject get(IocKey key);

    /**
     * 获取指定的IocObject列表，每一个列表元素都是经过注入初始化的
     * @param keys
     * @return
     */
    IocObject[] fetchValues(IocKey[] keys);

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
     * 获取到一个方法代理
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


    IocScope getScope(Scope scope);

    /**
     * wait for async load complete
     */
    void waitFor();

    void setLoadComplete();


    ClassLoader getClassLoader();
}
