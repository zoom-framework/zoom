package org.zoomdev.zoom.ioc;

public interface IocEventListener {
    /**
     * 对象创建成功
     *
     * @param scope
     * @param object
     */
    void onObjectCreated(IocScope scope, IocObject object);

    /**
     * 对象注入成功
     *
     * @param scope
     * @param object
     */
    void onObjectInjected(IocScope scope, IocObject object);

    /**
     * 对象销毁
     *
     * @param scope
     * @param object
     */
    void onObjectDestroy(IocScope scope, IocObject object);
}
