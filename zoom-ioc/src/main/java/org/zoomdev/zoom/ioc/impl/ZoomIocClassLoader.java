package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZoomIocClassLoader extends IocBase implements IocClassLoader,Destroyable {

    private Map<IocKey, IocClass> pool = new ConcurrentHashMap<IocKey, IocClass>();

	private ClassEnhancer classEnhancer;

	public ZoomIocClassLoader(IocContainer ioc){
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
                if(key.isInterface()){
                    throw new IocException("找不到"+key.getType()+"对应的配置，是否已经在Ioc容器注册了?");
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

    @Override
    public IocClass append(final Class<?> baseType, final Object instance, final boolean initialized) {
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
    public IocClass append(Class<?> baseType, Object instance) {
        return append(baseType,instance,false);
    }

    @Override
    public IocClass append(Object moduleInstance, Method method) {
        ZoomIocConstructor constructor = ZoomIocConstructor.createFromIocBean(moduleInstance, method, this);
		ZoomBeanIocClass iocClass = new ZoomBeanIocClass( ioc,this, constructor, constructor.getKey());
        constructor.setIocClass(iocClass);
		pool.put(constructor.getKey(), iocClass);

        return iocClass;
	}



}
