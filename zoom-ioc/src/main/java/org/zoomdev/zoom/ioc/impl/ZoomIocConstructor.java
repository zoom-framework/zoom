package org.zoomdev.zoom.ioc.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

abstract class ZoomIocConstructor implements IocConstructor {

    static ZoomIocConstructor createFromIocBean(Object target, Method method, IocClassLoader classLoader) {
        try {
            IocBean iocBean = method.getAnnotation(IocBean.class);
            String initialize = iocBean.initialize();
            String name = iocBean.name();
            String destroy = iocBean.destroy();
            IocKey key = new ZoomIocKey(name, method.getReturnType());
            return new IocBeanConstructor(key,
                    ZoomIocContainer.parseParameterValues(target, method, classLoader), target, method,
                    initialize, destroy);
        } catch (Exception e) {
            throw new IocException("不能创建IocBean:[" + target + "] " + method, e);
        }
    }


    static class ReflectIocEvent implements IocEvent {

        private Method method;

        ReflectIocEvent(Method method) {
            this.method = method;
        }

        @Override
        public void call(Object target) {
            try {
                method.invoke(target);
            } catch (Exception e) {
                throw new ZoomException(e);
            }
        }
    }

    static IocEvent createEvent(Class<?> type, Method method) {
        return new ReflectIocEvent(method);
    }

    static IocEvent createEvent(Class<?> type, String name) {
        List<Method> methods = CachedClasses.getPublicMethods(type, name);
        if (methods.size() == 0) {
            throw new IocException("在创建" + name + "的时候失败" + type + ",未找到相关方法");
        }
        if (methods.size() == 1) {
            if (Classes.getParameterCount(methods.get(0)) > 0) {
                throw new IocException("在创建" + name + "的时候失败" + type + ",必须提供无参函数");
            }
            return createEvent(type, methods.get(0));
        }
        for (Method method : methods) {
            if (Classes.getParameterCount(method) == 0) {
                return createEvent(type, method);
            }
        }
        throw new IocException("在创建" + name + "的时候失败" + type + ",找到了多个方法，不能确定是哪一个个");
    }

    static ZoomIocConstructor createFromInstance(Class<?> type, Object value, boolean initialized) {
        return new IocInstanceConstructor(type, value, initialized);
    }

    static ZoomIocConstructor createFromClass(
            Class<?> type,
            IocKey key,
            IocClassLoader iocClassLoader,
            ClassEnhancer classEnhancer) {

        type = classEnhancer.enhance(type);

        Constructor<?>[] constructors = type.getConstructors();
        Constructor<?> constructor;
        if (constructors.length == 1) {
            constructor = constructors[0];
        } else {
            //如果有多个构造函数，那么寻找无参数的
            constructor = Classes.findNoneParameterConstructor(type);
            if (constructor == null) {
                throw new IocException("不能创建IocConstructor,请提供无参数构造函数，否则系统无法判断应该使用哪一个构造函数来创建对象.");
            }
        }


        return new IocConstructorContructor(key,
                ZoomIocContainer.parseParameterValues(
                        type.getClassLoader(),
                        constructor.getParameterAnnotations(),
                        constructor.getParameterTypes(),
                        iocClassLoader), constructor);
    }

    static class IocInstanceConstructor extends ZoomIocConstructor {

        private Object instance;
        private boolean inited;

        public IocInstanceConstructor(Class<?> type, Object instance, boolean inited) {
            super(new ZoomIocKey(type), ZoomIocContainer.EMPTY_VALUES);
            assert (instance != null);
            this.inited = inited;
            this.instance = instance;
        }

        @Override
        public IocObject newInstance() {
            return ZoomIocObject.wrap(iocClass, instance, inited);
        }
    }

    static class IocConstructorContructor extends ZoomIocConstructor {

        private Constructor<?> constructor;


        public IocConstructorContructor(IocKey key, IocValue[] parameterKeys, Constructor<?> constructor) {
            super(key, parameterKeys);
            this.constructor = constructor;
        }

        @Override
        public IocObject newInstance() {
            try {
                // IocObject[] values = iocClass.getValues(parameterKeys);
                Object[] param = ZoomIocContainer.getValues(iocClass.getIoc(), parameterKeys);
                return ZoomIocObject.wrap(iocClass, constructor.newInstance(param));
            } catch (Exception e) {
                throw new IocException(e);
            }
        }

    }


    /**
     * Module的IocBean标注的方法
     *
     * @author jzoom
     */
    static class IocBeanConstructor extends ZoomIocConstructor implements IocConstructor {

        private Method method;
        private Object target;
        private String initialize;
        private String destroy;

        public IocBeanConstructor(IocKey key,
                                  IocValue[] parameterKeys,
                                  Object target, Method method, String init, String destroy) {
            super(key, parameterKeys);
            assert (target != null && method != null);
            this.method = method;
            this.target = target;
            this.initialize = init;
            this.destroy = destroy;
        }

        @Override
        public IocObject newInstance() {
            try {
                Object[] values = ZoomIocContainer.getValues(iocClass.getIoc(), parameterKeys);
                //IocObject[] values = iocClass.getIoc().fetchValues(parameterKeys);
                Object bean = method.invoke(target, values);

                IocEvent iocDestroy = null;
                IocEvent iocInit = null;

                if (!StringUtils.isEmpty(initialize)) {
                    iocInit = createEvent(bean.getClass(), initialize);
                }

                if (!StringUtils.isEmpty(destroy)) {
                    iocDestroy = createEvent(bean.getClass(), destroy);
                }

                return ZoomIocObject.wrap(iocClass, bean, iocInit, iocDestroy);
            } catch (Throwable e) {
                throw new IocException("创建ioc对象失败,method:" + method, e);
            }
        }
    }

    protected IocKey key;

    protected IocValue[] parameterKeys;


    protected IocClass iocClass;

    ZoomIocConstructor(IocKey key, IocValue[] parameterKeys) {
        assert (key != null && parameterKeys != null);
        this.key = key;
        this.parameterKeys = parameterKeys;

    }

    @Override
    public IocValue[] getParameterValues() {
        return parameterKeys;
    }

    public void setIocClass(IocClass iocClass) {
        this.iocClass = iocClass;
    }

    @Override
    public IocKey getKey() {
        return key;
    }

//    @Override
//    public IocKey[] getParameterKeys() {
//        return parameterKeys;
//    }

}
