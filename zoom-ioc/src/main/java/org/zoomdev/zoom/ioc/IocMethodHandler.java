package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;

public interface IocMethodHandler {
    void create(IocObject target, IocMethodProxy method);
    void inject(IocObject target,IocMethodProxy method);
    void destroy(IocObject target, IocMethodProxy method);

    boolean accept(Method method);
}
