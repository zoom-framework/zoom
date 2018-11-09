package com.jzoom.zoom.ioc;

public interface IocEventListener {
    /**
     * 对象创建成功
     * @param scope
     * @param object
     */
    void onObjectCreated(IocScope scope,IocObject object);
}
