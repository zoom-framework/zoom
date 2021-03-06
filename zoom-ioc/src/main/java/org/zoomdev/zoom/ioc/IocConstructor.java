package org.zoomdev.zoom.ioc;

/**
 * ioc容器构造器，目的是创建一个Ioc对象，不包含注入过程
 */
public interface IocConstructor {

    /**
     * 返回值的key
     *
     * @return
     */
    IocKey getKey();

    IocValue[] getParameterValues();

    IocObject newInstance();

}
