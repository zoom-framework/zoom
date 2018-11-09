package com.jzoom.zoom.ioc;

/**
 * ioc事件接口
 * ioc生命周期回调
 * {@link com.jzoom.zoom.common.Destroyable}
 * {@link com.jzoom.zoom.common.Initializeable}
 *
 * @author jzoom
 *
 */
public interface IocEvent {
    /**
     * @param target
     */
    void call(Object target);
}
