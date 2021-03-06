package org.zoomdev.zoom.ioc.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.ioc.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZoomIocContainer implements IocContainer, IocEventListener {

    private GlobalScope globalScope;

    private ZoomIocClassLoader iocClassLoader;

    private List<IocEventListener> eventListeners =
            Collections.synchronizedList(new ArrayList<IocEventListener>());


    private final ClassLoader classLoader;

    public ZoomIocContainer() {
        this(ZoomIocContainer.class.getClassLoader());
    }

    public ZoomIocContainer(ClassLoader classLoader) {
        this.classLoader = classLoader;
        globalScope = new GlobalScope(this, this);
        this.iocClassLoader = new ZoomIocClassLoader(this);
        this.iocClassLoader.setClassEnhancer(new NoneEnhancer());
        getIocClassLoader().append(IocContainer.class, this, true, IocBean.SYSTEM);

    }

    public ZoomIocContainer(IocContainer parent, ClassLoader classLoader) {
        this.classLoader = classLoader;
        IocScope parentScope = parent.getScope(Scope.APPLICATION);
        IocClassLoader parentClassLoader = parent.getIocClassLoader();
        List<IocEventListener> eventListeners = parent.getEventListeners();

        globalScope = new GroupScope(this, this, parentScope);
        this.iocClassLoader = new GroupClassLoader(this, parentClassLoader);
        this.iocClassLoader.setClassEnhancer(new NoneEnhancer());
        this.eventListeners.addAll(eventListeners);
        getIocClassLoader().append(IocContainer.class, this, true, IocBean.SYSTEM);
    }


    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }


    @Override
    public void destroy() {

        this.iocClassLoader.destroy();
        globalScope.destroy();
        this.eventListeners.clear();

    }

    @Override
    public List<IocEventListener> getEventListeners() {
        return eventListeners;
    }

    @Override
    public IocObject fetch(IocKey key) {
        return fetch(globalScope, key, true);
    }

    @Override
    public <T> T fetch(Class<T> type) {
        return (T) fetch(new ZoomIocKey(type)).get();
    }

    @Override
    public IocObject get(IocKey key) {
        return fetch(globalScope, key, false);
    }

    @Override
    public IocObject[] fetchValues(IocKey[] keys) {
        IocObject[] values = new IocObject[keys.length];
        for (int i = 0, c = keys.length; i < c; ++i) {
            values[i] = fetch(keys[i]);
        }
        return values;
    }

    private void initObject(ZoomIocObject obj, IocScope scope) {


        if (!obj.inited) {
            obj.inited = true;
            inject(obj, scope);
            onObjectInjected(scope, obj);
        }
    }

    private void inject(ZoomIocObject obj, IocScope scope) {
        //先初始化构造器中没有被初始化的
        IocClass iocClass = obj.getIocClass();
        IocConstructor iocConstructor = iocClass.getIocConstructor();


        for (IocValue value : iocConstructor.getParameterValues()) {
            if (value instanceof ZoomIocKeyValue) {
                ZoomIocObject childObject = (ZoomIocObject) scope.get(((ZoomIocKeyValue) value).getKey());
                initObject(childObject, scope);
            }

        }


        IocField[] fields = iocClass.getIocFields();
        if (fields != null) {
            for (IocField field : fields) {
                field.inject(obj);
            }
        }

        IocMethod[] methods = iocClass.getIocMethods();
        if (methods != null) {
            for (IocMethod method : methods) {
                if (method == null) {
                    //可能是空的
                    continue;
                }
                method.inject(obj);
            }
        }

        obj.initialize();
    }

    public synchronized IocObject fetch(IocScope scope, IocKey key, boolean inject) {
        try {
            ZoomIocObject obj = (ZoomIocObject) scope.get(key);
            IocClass iocClass = null;
            if (obj == null) {
                iocClass = this.iocClassLoader.get(key);
                if (iocClass == null) {
                    if (!key.hasName() && !key.isInterface()) {
                        iocClass = iocClassLoader.append(key.getType());
                    } else {
                        throw new IocException("找不到IocClass key:" + key + " 请提供ioc的配置");
                    }
                }
                obj = (ZoomIocObject) iocClass.newInstance();
                if (obj == null) {
                    throw new IocException("创建对象失败");
                }
            }
            if (inject) {
                initObject(obj, scope);
            }
            return obj;
        } catch (Throwable e) {
            throw new IocException("获取ioc对象失败" + key, e);
        }
    }


    static final Object[] EMPTY = new Object[0];
    static final IocKey[] EMPTY_KEYS = new IocKey[0];

    static final IocValue[] EMPTY_VALUES = new IocValue[0];

    public static Object[] getValues(IocContainer ioc, IocValue... objects) {

        if (objects.length == 0) {
            return EMPTY;
        }
        Object[] values = new Object[objects.length];
        for (int i = 0, c = objects.length; i < c; ++i) {
            values[i] = objects[i].getValue(ioc).get();
        }
        return values;
    }

    public static Object[] getValues(IocObject... objects) {
        if (objects.length == 0) {
            return EMPTY;
        }
        Object[] values = new Object[objects.length];
        for (int i = 0, c = objects.length; i < c; ++i) {
            values[i] = objects[i].get();
        }
        return values;
    }

    public static IocValue[] parseParameterValues(Object target, Method method, IocClassLoader classLoader) {
        return parseParameterValues(target.getClass().getClassLoader(), method.getParameterAnnotations(),
                method.getParameterTypes(), classLoader);
    }

    public static IocValue[] parseParameterValues(
            ClassLoader classLoader,
            Annotation[][] methodAnnotations,
            Class<?>[] types,
            IocClassLoader iocClassLoader) {
        int index = 0;
        IocValue[] args = new IocValue[types.length];
        IocValue arg;
        for (Class<?> type : types) {
            Inject paramInject = null;
            Annotation[] annotations = methodAnnotations[index];
            for (Annotation annotation : annotations) {
                if (annotation instanceof Inject) {
                    paramInject = (Inject) annotation;
                }
            }
            if (paramInject != null) {
                if (!StringUtils.isEmpty(paramInject.value())) {
                    arg = new ZoomIocKeyValue(new ZoomIocKey(paramInject.value(), type));
                } else if (!StringUtils.isEmpty(paramInject.config())) {
                    arg = new ZoomConfigValue(paramInject.config(), type);
                } else {
                    arg = new ZoomIocKeyValue(new ZoomIocKey(type));
                    if (!type.isInterface()) {
                        iocClassLoader.append(type);
                    }
                }
            } else {
                arg = new ZoomIocKeyValue(new ZoomIocKey(type));
                if (!type.isInterface()) {
                    iocClassLoader.append(type);
                }
            }
            args[index++] = arg;
        }
        return args;
    }
//    public static IocKey[] parseParameterKeys(
//            ClassLoader classLoader,
//            Annotation[][] methodAnnotations,
//            Class<?>[] types,
//            IocClassLoader iocClassLoader) {
//        int index = 0;
//        IocKey[] args = new IocKey[types.length];
//        IocKey arg;
//        for (Class<?> type : types) {
//            Inject paramInject = null;
//            Annotation[] annotations = methodAnnotations[index];
//            for (Annotation annotation : annotations) {
//                if (annotation instanceof Inject) {
//                    paramInject = (Inject) annotation;
//                }
//            }
//            if (paramInject != null && !StringUtils.isEmpty(paramInject.value())) {
//                arg = new ZoomIocKey(paramInject.value(), type);
//            } else {
//                arg = new ZoomIocKey(type);
//                if(!type.isInterface()){
//                    iocClassLoader.append(type);
//                }
//            }
//            args[index++] = arg;
//        }
//        return args;
//    }

//    public static IocKey[] parseParameterKeys(Object target, Method method, IocClassLoader classLoader) {
//        return parseParameterKeys(target.getClass().getClassLoader(), method.getParameterAnnotations(),
//                method.getParameterTypes(), classLoader);
//    }

    /**
     * 解析fields
     *
     * @param type
     * @return
     */
    public static IocField[] parseFields(IocContainer ioc, Class<?> type, IocClassLoader classLoader) {
        Field[] fields = CachedClasses.getFields(type);
        if (fields == null || fields.length == 0) {
            return null;
        }
        List<IocField> iocFields = new ArrayList<IocField>();
        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                try {
                    IocField iocField = createIocField(ioc, inject, field, classLoader);
                    iocFields.add(iocField);
                } catch (Throwable t) {
                    throw new IocException("注入字段失败" + field, t);
                }

            }
        }
        if (iocFields.size() == 0) return null;
        return iocFields.toArray(new IocField[iocFields.size()]);
    }


    @Override
    public IocMethod getMethod(IocClass iocClass, Method target) {
        IocMethod method = iocClass.getIocMethod(target);
        if (method != null) {
            return method;
        }
        return createIocMethod(
                this,
                iocClass,
                iocClass.getKey().getType(), target);
    }

    @Override
    public IocMethodProxy getMethodProxy(IocClass iocClass, Method target) {
        IocMethod method = iocClass.getIocMethod(target);
        if (method != null) {
            return new IocIocMethodPorxy(method);
        }

        return new ReflectIocMethodProxy(
                this, iocClass, target);
    }

    @Override
    public void release(Scope scope) {

    }

    @Override
    public IocScope getScope(Scope scope) {
        return globalScope;
    }

    private volatile boolean loaded;

    private Object lock = new Object();

    @Override
    public void waitFor() {
        while (!loaded) {
            try {
                synchronized (lock) {
                    lock.wait(200);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    public void setLoadComplete() {
        loaded = true;
        synchronized (lock) {
            lock.notify();
        }

    }


    /**
     * 将来改成可配置的
     *
     * @param inject
     * @param field
     * @param classLoader
     * @return
     */
    private static IocField createIocField(IocContainer ioc, Inject inject, Field field, IocClassLoader classLoader) {
//		IocField result;
//		if( iocInjectorFactory != null && ((result = iocInjectorFactory.createIocField(inject,key,field)) != null) ){
//			return result;
//		}

        Class<?> fieldType = field.getType();
        if (!StringUtils.isEmpty(inject.config())) {
            //配置
            String name = inject.config();
            IocKey key = new ZoomIocKey(name, fieldType);
            return new ZoomConfigIocField(ioc, field, name);
        } else {
            String name = inject.value();
            IocKey key = new ZoomIocKey(name, fieldType);
            if (!key.hasName()) {
                classLoader.append(key.getType());

            }
            return new ZoomBeanIocField(ioc, field, key);
        }


    }

    /**
     * 解析methods
     *
     * @param type
     * @return
     */
    public static IocMethod[] parseMethods(
            IocContainer ioc,
            IocClass iocClass,
            Class<?> type,
            IocClassLoader classLoader) {
        if (type.getSimpleName().endsWith("$Enhance")) {
            try {
                String className = type.getName();
                className = className.substring(0, className.length() - 8);
                type = Class.forName(className, false, classLoader.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new ZoomException();
            }
        }
        Method[] methods = CachedClasses.getPublicMethods(type);
        if (methods == null || methods.length == 0) return null;
        List<IocMethod> iocMethods = new ArrayList<IocMethod>();
        for (Method method : methods) {
            Inject inject = method.getAnnotation(Inject.class);
            if (inject != null) {
                try {
                    IocMethod iocMethod = createIocMethod(ioc, iocClass, type, method);
                    iocMethods.add(iocMethod);
                } catch (Throwable t) {
                    throw new IocException("注入方法失败" + method, t);
                }
            }
        }

        if (iocMethods.size() == 0)
            return null;
        return iocMethods.toArray(new IocMethod[iocMethods.size()]);
    }

    public static IocMethod createIocMethod(
            IocContainer ioc,
            IocClass iocClass,
            Class<?> type,
            Method method) {
        try {
            IocValue[] values = parseParameterValues(
                    type.getClassLoader(),
                    method.getParameterAnnotations(),
                    method.getParameterTypes(), iocClass.getIocClassLoader());
            return new ZoomIocMethod(ioc, iocClass, values, method);
        } catch (Throwable t) {
            throw new IocException("注入方法失败" + method, t);
        }
    }

    public IocClassLoader getIocClassLoader() {
        return iocClassLoader;
    }

    @Override
    public void setClassEnhancer(ClassEnhancer enhancer) {
        this.iocClassLoader.setClassEnhancer(enhancer);
    }

    @Override
    public void addEventListener(IocEventListener listener) {
        eventListeners.add(listener);
    }


    @Override
    public void onObjectCreated(IocScope scope, IocObject object) {

        for (IocEventListener listener : eventListeners) {
            listener.onObjectCreated(scope, object);
        }

    }

    @Override
    public void onObjectInjected(IocScope scope, IocObject object) {
        for (IocEventListener listener : eventListeners) {
            listener.onObjectInjected(scope, object);
        }
    }

    @Override
    public void onObjectDestroy(IocScope scope, IocObject object) {
        for (IocEventListener listener : eventListeners) {
            listener.onObjectDestroy(scope, object);
        }
    }
}
