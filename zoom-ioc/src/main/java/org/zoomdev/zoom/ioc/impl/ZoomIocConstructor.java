package org.zoomdev.zoom.ioc.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

abstract class ZoomIocConstructor implements IocConstructor {

    static ZoomIocConstructor createFromIocBean(Object target, Method method, IocClassLoader classLoader) {
        IocBean iocBean = method.getAnnotation(IocBean.class);
        String initialize = iocBean.initialize();
        String name = iocBean.name();
        String destroy = iocBean.destroy();
        IocKey key = new ZoomIocKey(name, method.getReturnType());
        return new IocBeanConstructor(key,
                SimpleIocContainer.parseParameterKeys(target, method, classLoader), target, method,
                initialize, destroy);
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
                throw new RuntimeException(e);
            }
        }
    }

    static IocEvent createEvent(Class<?> type, Method method) {
        return new ReflectIocEvent(method);
    }

    static IocEvent createEvent(Class<?> type, String name) {
        List<Method> methods = Classes.findPublicMethods(type, name);
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
        if (constructors.length == 0) {
            return new IocClassConstructor(key, SimpleIocContainer.EMPTY_KEYS, type);
        }

        if (constructors.length == 1) {
            Constructor<?> constructor = constructors[0];
            return new IocConstructorContructor(key,
                    SimpleIocContainer.parseParameterKeys(
                            type.getClassLoader(),
                            constructor.getParameterAnnotations(),
                            constructor.getParameterTypes(),
                            iocClassLoader), constructor);
        }

        //如果有多个构造函数，那么寻找无参数的
        Constructor<?> constructor = Classes.findNoneParameterConstructor(type);
        if (constructor == null) {
            throw new IocException("不能创建IocConstructor,请提供无参数构造函数，否则系统无法判断应该使用哪一个构造函数来创建对象.");
        }


        return new IocClassConstructor(key, SimpleIocContainer.EMPTY_KEYS, type);
    }

    static class IocInstanceConstructor extends ZoomIocConstructor {

        private Object instance;
        private boolean inited;

        public IocInstanceConstructor(Class<?> type, Object instance, boolean inited) {
            super(new ZoomIocKey(type), SimpleIocContainer.EMPTY_KEYS);
            assert (instance != null);
            this.inited = inited;
            this.instance = instance;
        }

        @Override
        public IocObject newInstance(IocObject[] values) {
            return ZoomIocObject.wrap(iocClass, instance, inited);
        }
    }

    static class IocConstructorContructor extends ZoomIocConstructor {

        private Constructor<?> constructor;


        public IocConstructorContructor(IocKey key, IocKey[] parameterKeys, Constructor<?> constructor) {
            super(key, parameterKeys);
            this.constructor = constructor;
        }

        @Override
        public IocObject newInstance(IocObject[] values) {
            try {
                return ZoomIocObject.wrap(iocClass, constructor.newInstance(SimpleIocContainer.getValues(
                        values
                )));
            } catch (Exception e) {
                throw new IocException(e);
            }
        }

    }

    static class IocClassConstructor extends ZoomIocConstructor {

        private Class<?> type;


        public IocClassConstructor(IocKey key, IocKey[] parameterKeys, Class<?> type) {
            super(key, parameterKeys);
            this.type = type;

            if (type.isInterface()) {
                throw new IocException("在创建ioc构造器的时候失败," + key.getType() + ":类型" + type + "不能作为类初始化参数,必须是实际类");
            }
        }

        @Override
        public IocObject newInstance(IocObject[] values) {
            try {
                return ZoomIocObject.wrap(iocClass, type.newInstance());
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
                                  IocKey[] parameterKeys,
                                  Object target, Method method, String init, String destroy) {
            super(key, parameterKeys);
            assert (target != null && method != null);
            this.method = method;
            this.target = target;
            this.initialize = init;
            this.destroy = destroy;
        }

        @Override
        public IocObject newInstance(IocObject[] values) {
            try {
                Object bean = method.invoke(target, SimpleIocContainer.getValues(values));

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

    protected IocKey[] parameterKeys;


    protected IocClass iocClass;

    ZoomIocConstructor(IocKey key, IocKey[] parameterKeys) {
        assert (key != null && parameterKeys != null);
        this.key = key;
        this.parameterKeys = parameterKeys;

    }

    public void setIocClass(IocClass iocClass) {
        this.iocClass = iocClass;
    }

    @Override
    public IocKey getKey() {
        return key;
    }

    @Override
    public IocKey[] getParameterKeys() {
        return parameterKeys;
    }

}
