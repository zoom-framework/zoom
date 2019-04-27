package org.zoomdev.zoom.ioc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZoomIocClassLoader extends IocBase implements IocClassLoader, Destroyable {

    private Map<IocKey, IocClass> pool = new ConcurrentHashMap<IocKey, IocClass>(64);

    private ClassEnhancer classEnhancer;

    public ZoomIocClassLoader(IocContainer ioc) {
        super(ioc);

    }

    @Override
    public IocClass get(IocKey key) {
        return pool.get(key);
    }

    @Override
    public void destroy() {
        Classes.destroy(pool);
    }

    @Override
    public IocClass append(final Class<?> type) {
        final IocKey key = new ZoomIocKey(type);
        return SingletonUtils.liteDoubleLockMap(pool, key, new SingletonUtils.SingletonInit<IocClass>() {
            @Override
            public IocClass create() {
                if (key.isInterface()) {
                    throw new IocException("找不到" + key.getType() + "对应的配置，是否已经在Ioc容器注册了?");
                }
                ZoomIocConstructor constructor = ZoomIocConstructor
                        .createFromClass(type, key, ZoomIocClassLoader.this, classEnhancer);
                IocClass iocClass = new ZoomBeanIocClass(
                        ioc,
                        ioc.getScope(IocContainer.Scope.APPLICATION),
                        ZoomIocClassLoader.this,
                        constructor,
                        constructor.getKey(),
                        IocBean.USER);
                constructor.setIocClass(iocClass);
                return iocClass;
            }
        });
    }

    @Override
    public void setClassEnhancer(ClassEnhancer enhancer) {
        this.classEnhancer = enhancer;
    }

    @Override
    public ClassLoader getClassLoader() {
        return ioc.getClassLoader();
    }


    private static final Log log = LogFactory.getLog(ZoomIocClassLoader.class);

    @Override
    public List<IocClass> appendModule(Class moduleClass) {
        if(log.isDebugEnabled())
            log.debug(String.format("初始化Module [%s]", moduleClass));
        Object module = Classes.newInstance(moduleClass);
        append(moduleClass, module,false,IocBean.SYSTEM);
        List<IocClass> list = new ArrayList<IocClass>();
        //bean
        Method[] methods = CachedClasses.getPublicMethods(moduleClass);
        for (Method method : methods) {
            IocBean bean = method.getAnnotation(IocBean.class);
            if (bean != null) {
                append(module, method, bean.order());
            }
        }
        return list;

    }

    @Override
    public <T> IocClass append(final Class<T> baseType, final T instance, final boolean initialized, final int order) {
        IocKey key = new ZoomIocKey(baseType);
        return SingletonUtils.liteDoubleLockMap(pool, key, new SingletonUtils.SingletonInit<IocClass>() {
            @Override
            public IocClass create() {
                ZoomIocConstructor constructor = ZoomIocConstructor.createFromInstance(baseType, instance, initialized);
                IocClass iocClass = new ZoomBeanIocClass(
                        ioc,
                        ioc.getScope(IocContainer.Scope.APPLICATION),
                        ZoomIocClassLoader.this,
                        constructor,
                        constructor.getKey(), order);
                constructor.setIocClass(iocClass);
                return iocClass;
            }
        });
    }



    @Override
    public IocClass append(Object moduleInstance, Method method, int order) {
        ZoomIocConstructor constructor = ZoomIocConstructor.createFromIocBean(moduleInstance, method, this);
        ZoomBeanIocClass iocClass = new ZoomBeanIocClass(
                ioc,
                ioc.getScope(IocContainer.Scope.APPLICATION),
                this,
                constructor,
                constructor.getKey(), order);
        constructor.setIocClass(iocClass);
        pool.put(constructor.getKey(), iocClass);

        return iocClass;
    }


}
