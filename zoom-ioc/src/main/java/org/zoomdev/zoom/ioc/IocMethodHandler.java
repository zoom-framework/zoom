package org.zoomdev.zoom.ioc;

import java.lang.reflect.Method;

public interface IocMethodHandler {
    void visit(IocObject target, Method method, IocMethodProxy proxy);

    boolean accept(Method method);
}
