package org.zoomdev.zoom.ioc;

public interface IocEventListener {
    /**
     * 对象创建成功
     * @param scope
     * @param object
     */
    void onObjectCreated(IocScope scope,IocObject object);

    void onObjectDestroy(IocScope scope,IocObject object);
}
