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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZoomIocClassLoader extends IocBase implements IocClassLoader, Destroyable {

    private Map<IocKey, IocClass> pool = new ConcurrentHashMap<IocKey, IocClass>();

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
                        ZoomIocClassLoader.this,
                        constructor,
                        constructor.getKey());
                constructor.setIocClass(iocClass);
                return iocClass;
            }
        });
    }

    @Override
    public void setClassEnhancer(ClassEnhancer enhancer) {
        this.classEnhancer = enhancer;
    }


    private static final Log log = LogFactory.getLog(ZoomIocClassLoader.class);

    @Override
    public void appendModule(Class moduleClass) {
        try {
            log.info(String.format("初始化Module [%s]", moduleClass));
            Object module = Classes.newInstance(moduleClass);
            append(moduleClass, module);
            //bean
            Method[] methods = CachedClasses.getPublicMethods(moduleClass);
            for (Method method : methods) {
                IocBean bean = method.getAnnotation(IocBean.class);
                if (bean != null) {
                    append(module, method);
                }
            }
        } catch (Exception e) {
            throw new IocException("Module初始化失败，Module必须有一个默认构造函数", e);
        }
    }

    @Override
    public <T> IocClass append(final Class<T> baseType, final T instance, final boolean initialized) {
        IocKey key = new ZoomIocKey(baseType);
        return SingletonUtils.liteDoubleLockMap(pool, key, new SingletonUtils.SingletonInit<IocClass>() {
            @Override
            public IocClass create() {
                ZoomIocConstructor constructor = ZoomIocConstructor.createFromInstance(baseType, instance, initialized);
                IocClass iocClass = new ZoomBeanIocClass(
                        ioc,
                        ZoomIocClassLoader.this,
                        constructor,
                        constructor.getKey());
                constructor.setIocClass(iocClass);
                return iocClass;
            }
        });
    }

    @Override
    public <T> IocClass append(Class<T> baseType, T instance) {
        return append(baseType, instance, false);
    }

    @Override
    public IocClass append(Object moduleInstance, Method method) {
        ZoomIocConstructor constructor = ZoomIocConstructor.createFromIocBean(moduleInstance, method, this);
        ZoomBeanIocClass iocClass = new ZoomBeanIocClass(ioc, this, constructor, constructor.getKey());
        constructor.setIocClass(iocClass);
        pool.put(constructor.getKey(), iocClass);

        return iocClass;
    }


}
