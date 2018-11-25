package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Method;

/**
 * 一开始，并不知道所有的Constructor,必须要等实例化之后才知道
 *
 * @author jzoom
 */
public class ZoomBeanIocClass extends ZoomIocClass {

    private IocContainer iocContainer;

    public ZoomBeanIocClass(
            IocContainer ioc,
            IocScope scope,
            IocClassLoader classLoader,
            IocConstructor constructor,
            IocKey key) {
        super(ioc, scope,classLoader, constructor, key);
    }

    private boolean injectorCreated = false;


    private void createInjector(IocContainer ioc, IocScope scope, Object instance) {
        injectorCreated = true;
        methods = ZoomIocContainer.parseMethods(ioc, this, instance.getClass(), classLoader);
        fields = ZoomIocContainer.parseFields(ioc, instance.getClass(), classLoader);
    }


    @Override
    public IocObject newInstance() {
        IocObject obj = fetch(constructor);
        Object instance = obj.get();

        if (!injectorCreated) {
            createInjector(ioc, scope, instance);
        }


        return obj;
    }

    @Override
    public IocMethod getMethod(Method method) {
        if (getIocMethods() != null) {
            for (IocMethod iocMethod : getIocMethods()) {
                if (iocMethod.getMethod() == method) {
                    return iocMethod;
                }
            }
        }

        return null;
    }

}
