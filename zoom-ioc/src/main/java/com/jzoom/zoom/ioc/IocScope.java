package com.jzoom.zoom.ioc;

/**
 * 表示一个存储ioc对象的范围空间
 */
public interface IocScope {

    /**
     * 获取ioc对象
     *
     * @param key
     * @return
     */
    IocObject get(IocKey key);

    /**
     * 设置ioc对象
     *
     * @param key
     * @param value
     * @return
     */
    IocObject put(IocKey key, IocObject value);


    /**
     * 获取ioc容器
     *
     * @return
     */
    IocContainer getIoc();
}
