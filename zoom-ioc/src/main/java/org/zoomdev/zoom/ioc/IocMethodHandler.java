package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;

public interface IocMethodHandler {
    void create(IocObject target, Method method, IocMethodProxy proxy);
    void destroy(IocObject target,Method method);
    boolean accept(Method method);
}
