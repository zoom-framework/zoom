package org.zoomdev.zoom.common.utils;

import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.designpattern.SingletonUtils.SingletonInit;
import org.zoomdev.zoom.common.filter.MethodFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class CachedClasses {

    private static class ClassHolder {
        Field[] fields;
        Method[] publicMethods;

        public void clear() {
            fields = null;
            publicMethods = null;
        }
    }

    private static Map<Class<?>, ClassHolder> map = new ConcurrentHashMap<Class<?>, CachedClasses.ClassHolder>();

    public static void clear() {
        for (Entry<?, ClassHolder> entry : map.entrySet()) {
            entry.getValue().clear();
        }
        map.clear();
    }

    public static Field[] getFields(Class<?> type) {
        ClassHolder holder = getHolder(type);
        if (holder.fields == null) {
            synchronized (holder) {
                if (holder.fields == null) {
                    List<Field> fields = Classes.getFields(type);
                    holder.fields = fields.toArray(new Field[fields.size()]);
                }

            }
        }
        return holder.fields;
    }

    public static ClassHolder getHolder(Class<?> type) {
        return SingletonUtils.liteDoubleLockMap(map, type, new SingletonInit<ClassHolder>() {
            @Override
            public ClassHolder create() {
                return new ClassHolder();
            }
        });
    }

    public static Method[] getPublicMethods(Class<?> type) {
        ClassHolder holder = getHolder(type);

        if (holder.publicMethods == null) {
            synchronized (holder) {
                if (holder.publicMethods == null) {
                    List<Method> methods = Classes.findPublicMethods(type);
                    holder.publicMethods = methods.toArray(new Method[methods.size()]);
                }
            }
        }
        return holder.publicMethods;
    }


    public static List<Method> getPublicMethods(Class<?> clazz, String name) {
        List<Method> list = new ArrayList<Method>();
        Method[] methods = getPublicMethods(clazz);
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                list.add(method);
            }
        }
        return list;
    }

    public static List<Method> getPublicMethods(Class<?> clazz, MethodFilter filter) {
        List<Method> list = new ArrayList<Method>();
        Method[] methods = getPublicMethods(clazz);
        for (Method method : methods) {
            if (filter.accept(method)) {
                list.add(method);
            }
        }
        return list;
    }

    public static Method getPublicMethod(Class<?> clazz, MethodFilter filter) {
        Method[] methods = getPublicMethods(clazz);
        for (Method method : methods) {
            if (filter.accept(method)) {
                return method;
            }
        }
        return null;
    }


}
