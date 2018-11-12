package org.zoomdev.zoom.dao.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DaoProxyHandler implements InvocationHandler {

    private Map<Method, DaoInvoker> map = new HashMap<Method, DaoInvoker>();


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DaoInvoker invoker = map.get(method);
        if (invoker == null) {
            return invoker.invoke(args);
        }

        return method.invoke(proxy, args);
    }
}
