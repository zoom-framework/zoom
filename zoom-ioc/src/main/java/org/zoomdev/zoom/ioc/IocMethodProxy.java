package org.zoomdev.zoom.ioc;

/**
 * 一个ioc方法的执行对象
 * 不必传入参数，参数由ioc容器处理
 */
public interface IocMethodProxy {

    /**
     * 执行ioc方法，并不一定会返回ioc对象，所以直接返回一个Object
     *
     * @param target
     * @return
     */
    Object invoke(IocObject target);
}
