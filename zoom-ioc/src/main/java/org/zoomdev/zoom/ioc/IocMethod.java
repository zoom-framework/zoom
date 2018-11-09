package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;


/**
 * IOC方法接口
 */
public interface IocMethod {

    IocKey[] getParameterKeys();

    Object invoke(IocObject obj, IocObject[] values);

    Method getMethod();
}
