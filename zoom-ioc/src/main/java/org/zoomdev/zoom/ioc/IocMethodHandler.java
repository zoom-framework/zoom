package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;

public interface IocMethodHandler {
    void create(IocObject target,IocMethod method);
    void destroy(IocObject target,IocMethod method);
    boolean accept(Method method);
}
