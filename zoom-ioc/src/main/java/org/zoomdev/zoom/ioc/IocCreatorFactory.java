package org.zoomdev.zoom.ioc;

/**
 * 针对接口进行创建对应的ios对象
 * 当然，需要编写一些规则
 */
public interface IocCreatorFactory {

    /**
     * 根据接口创建一个代理对象
     *
     * @param interfaceClass
     * @return
     */
    Object create(Class<?> interfaceClass);

}
