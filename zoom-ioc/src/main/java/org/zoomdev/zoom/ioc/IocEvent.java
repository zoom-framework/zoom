package org.zoomdev.zoom.ioc;

/**
 * ioc事件接口
 * ioc生命周期回调
 * {@link org.zoomdev.zoom.http.Destroyable}
 * {@link org.zoomdev.zoom.http.Initializeable}
 *
 * @author jzoom
 */
public interface IocEvent {
    /**
     * @param target
     */
    void call(Object target);
}
