package org.zoomdev.zoom.common.utils;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.common.Destroyable;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

public class Classes {

    public static Class<?> getClass(Type type) {
        assert (type != null);
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        }
        throw new RuntimeException("不能获取class " + type);
    }

    /**
     * 直接初始化一个对象
     *
     * @param type
     * @return
     */
    public static Object newInstance(Class<?> type) {
        try {
            return type.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("为了初始化该类" + type);
        }
    }

    public static RuntimeException makeThrow(Throwable e) {
        if (e instanceof InvocationTargetException) {
            return makeThrow(((InvocationTargetException) e).getTargetException());
        }
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(e);
    }



    public abstract static class ClassReference<T> {
        private Type type;

        public ClassReference() {
            type = getClass().getGenericSuperclass();
        }

        public Type getType() {
            return type;
        }

    }

    public static Type getParameterizedType(ClassReference<?> classReference) {
        return classReference.getType();
    }

    public static Type[] getParameterizedTypes(final Class<?> clazz) {
        if (clazz == null || "java.lang.Object".equals(clazz.getName()))
            return null;
        // 看看父类
        Type superclass = clazz.getGenericSuperclass();
        if (null != superclass && superclass instanceof ParameterizedType)
            return ((ParameterizedType) superclass).getActualTypeArguments();

        return null;
    }

    /**
     * 获取一个类的所有泛型信息
     *
     * @param clazz
     * @return
     */
    public static Type[] getAllParameterizedTypes(final Class<?> clazz) {
        if (clazz == null || "java.lang.Object".equals(clazz.getName()))
            return null;
        // 看看父类
        Type superclass = clazz.getGenericSuperclass();
        if (null != superclass && superclass instanceof ParameterizedType)
            return ((ParameterizedType) superclass).getActualTypeArguments();

        // 看看接口
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type inf : interfaces) {
            if (inf instanceof ParameterizedType) {
                return ((ParameterizedType) inf).getActualTypeArguments();
            }
        }
        return getAllParameterizedTypes(clazz.getSuperclass());
    }

    /**
     * dest 是否 是 src的包装类
     *
     * @param src
     * @param dest
     * @return
     */
    public static boolean isWapClass(Class<?> src, Class<?> dest) {
        try {
            return ((Class<?>) dest.getField("TYPE").get(null)) == src;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<Class<?>> getInsterfaces(Class<?> clazz) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        boolean working = true;
        do {
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces != null) {
                for (Class<?> class1 : interfaces) {
                    list.add(class1);
                }
            }
            clazz = clazz.getSuperclass();
            working = !clazz.equals(Object.class);
        } while (working);
        return list;
    }

    // 获取所有
    private static void getFields(List<Field> result, Class<?> clazz) {
        try {
            Field[] list = clazz.getDeclaredFields();
            for (Field field : list) {
                if ((field.getModifiers() & java.lang.reflect.Modifier.STATIC) > 0) {
                    continue;
                }
                if (field.getName().equals("class")) {
                    continue;
                }
                field.setAccessible(true);
                result.add(field);
            }
        } catch (Throwable e) {
            throw new RuntimeException(String.format("在获取%s的field的时候发生异常", clazz), e);
        }

    }

    public static Field findField(Class<?> clazz, String name) {
        try {
            Field result = null;
            try {
                result = clazz.getField(name);
                return result;
            } catch (NoSuchFieldException e) {

            }
            do {
                result = clazz.getDeclaredField(name);
                if (result != null) {
                    return result;
                }
                clazz = clazz.getSuperclass();
                if (clazz == Object.class) {
                    return null;
                }
            } while (true);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * @param clazz
     * @return
     */
    public static Field getField(Class<?> clazz, String name) {
        try {
            Field result = null;
            try {
                result = clazz.getField(name);
                return result;
            } catch (NoSuchFieldException e) {

            }
            do {
                result = clazz.getDeclaredField(name);
                if (result != null) {
                    return result;
                }
                clazz = clazz.getSuperclass();
                if (clazz == Object.class) {
                    throw new RuntimeException("Cannot find field " + name + " in " + clazz.getName());
                }
            } while (true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取一个类的所有Field
     *
     * @param clazz 对象类
     * @return 所有获取到的Field
     */
    static List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<Field>();
        Class<?> tmp = clazz;
        do {
            getFields(result, tmp);
            tmp = tmp.getSuperclass();
            if (tmp == Object.class) {
                break;
            }
        } while (true);
        return result;
    }

    /*
     * 取得某一类所在包的所有类名 不含迭代
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        // 将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packeageDir = new File(realClassLocation);
        if (packeageDir.isDirectory()) {
            String[] allClassName = packeageDir.list();
            return allClassName;
        }
        return null;
    }

    public static boolean isSimpleClass(Class<?> type) {
        if (type.isPrimitive()) {
            return true;
        } else if (type.getName().startsWith("java")) {
            return true;
        }
        return false;
    }

    /**
     * @return 当前对象是否为枚举
     */
    public static boolean isEnum(Class<?> type) {
        return type.isEnum();
    }

    public static boolean isSimple(Class<?> type) {
        return isString(type) || isBoolean(type) || isChar(type) || isNumber(type) || isDateTime(type) || isEnum(type);
    }

    public static boolean isDateTime(Class<?> type) {
        return Calendar.class.isAssignableFrom(type) || java.util.Date.class.isAssignableFrom(type)
                || java.sql.Date.class.isAssignableFrom(type) || java.sql.Time.class.isAssignableFrom(type);
    }

    public static boolean isString(Class<?> src) {
        return CharSequence.class.isAssignableFrom(src);
    }

    public static boolean isBoolean(Class<?> src) {
        return is(src, Boolean.class) || is(src, boolean.class);
    }

    public static boolean isChar(Class<?> src) {
        return is(src, Character.class) || is(src, char.class);
    }

    public static boolean isNumber(Class<?> src) {
        return Number.class.isAssignableFrom(src) || src == int.class || src == double.class || src == float.class
                || src == byte.class || src == short.class;
    }

    public static boolean is(Class<?> src, Class<?> desc) {
        return null != desc && src == desc;
    }

    /**
     * 获取field泛型类型
     */
    public static Class<?> getFieldListGenType(Field field) {
        java.lang.reflect.Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return (Class<?>) pType.getActualTypeArguments()[0];
        }
        return null;
    }

    /**
     * 获取所有的public方法
     *
     * @param type
     * @return
     */
    static List<Method> findPublicMethods(Class<?> type) {
        Method[] methods = type.getMethods();
        List<Method> list = new ArrayList<Method>(methods.length);
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            Class<?> cls = method.getDeclaringClass();
            if (cls != Object.class) {
                list.add(method);
            }
        }
        return list;
    }

    /**
     * 判断clazz是否实现了接口interfaceClass
     *
     * @param clazz
     * @param interfaceClass
     * @return
     */
    public static boolean hasInterface(Class<?> clazz, Class<?> interfaceClass) {
        Class<?>[] interfacesArray = clazz.getInterfaces();
        for (Class<?> i : interfacesArray) {
            if (i == interfaceClass)
                return true;
        }
        return false;
    }

    /**
     * 比较两个class是否是等价的，与==不同，这个方法可以判断包装类，如 isEqual(Integer.class,int.class)==true
     *
     * @param srcClass
     * @param destClass
     * @return
     */
    public static boolean isEqual(Class<?> srcClass, Class<?> destClass) {
        return srcClass == destClass || isWapClass(srcClass, destClass) || isWapClass(destClass, srcClass);
    }

    /**
     * 从class中提取所有相关的接口、超类
     *
     * @param clazz
     * @return
     */
    public static Class<?>[] extract(Class<?> clazz) {
        if (clazz.isInterface()) {
            return new Class<?>[]{clazz};
        }
        Set<Class<?>> set = new HashSet<Class<?>>();
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            for (Class<?> cls : interfaces) {
                set.add(cls);
            }
        }

        while (clazz != null) {
            set.add(clazz);
            clazz = clazz.getSuperclass();
        }

        return set.toArray(new Class<?>[set.size()]);

    }

    /**
     * 获取所有的setXXX方法
     *
     * @param type
     * @return
     */
    public static List<Method> getSetters(Class<?> type) {
        List<Method> methods = findPublicMethods(type);
        List<Method> setters = new ArrayList<Method>(methods.size());
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("set") && methodName.length() > 3 && method.getParameterTypes().length == 1) {
                setters.add(method);
            }
        }
        return setters;
    }

    /**
     * 设置静态字段的值
     *
     * @param clazz
     * @param name  字段名称
     * @param value
     * @throws Exception
     */
    public static void set(Class<?> clazz, String name, Object value) throws Exception {
        Field field = getField(clazz, name);
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * 获取错误原因，到非运行时错误为止
     *
     * @param e
     * @return
     */
    public static Throwable getCause(Throwable e) {
        if (e instanceof InvocationTargetException) {
            return getCause(((InvocationTargetException) e).getTargetException());
        }


        return e;
    }

    /**
     * 获取一个错误的明确的原因
     *
     * @return
     */
    public static String why(Throwable e) {
        Throwable t = getCause(e);
        if (t instanceof NoClassDefFoundError) {
            return "找不到类定义,是否缺少对应的.jar文件或者.class文件?" + ((NoClassDefFoundError) t).getMessage();
        }
        return "未知原因";
    }

    /**
     * 仅仅通过名称来查找static Method
     *
     * @param clazz
     * @param name
     */
    public static List<Method> getStaticMethods(Class<?> clazz, String name) {

        // 静态方法不能继承，所以只搜索本类
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> list = new ArrayList<Method>(methods.length);
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                list.add(method);
            }
        }

        return list;
    }

    /**
     * 通过名称查找public方法
     *
     * @param type
     * @param name
     * @return
     */
    public static List<Method> findPublicMethods(Class<?> type, String name) {
        assert (name != null);
        List<Method> methods = findPublicMethods(type);
        List<Method> result = new ArrayList<Method>();
        for (Method method : methods) {
            if (name.equals(method.getName())) {
                result.add(method);
            }
        }
        return result;
    }

    public static void destroy(Map<?, ?> map) {
        for (Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Destroyable) {
                ((Destroyable) value).destroy();
            }
        }
        map.clear();
    }

    public static void destroy(Collection<?> collection) {
        for (Object value : collection) {
            if (value instanceof Destroyable) {
                ((Destroyable) value).destroy();
            }
        }
        collection.clear();
    }

    public static <T> void destroy(T[] array) {
        for (Object value : array) {
            if (value instanceof Destroyable) {
                ((Destroyable) value).destroy();
            }
        }

        Arrays.fill(array, null);
    }

    public static void destroy(Object target) {
        if (target instanceof Destroyable) {
            ((Destroyable) target).destroy();
        }
    }

    /**
     * 直接调用Class.forName,避免抛出异常
     *
     * @param className
     * @return
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatStackTrace(Throwable result) {
        assert (result != null);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        result.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    /**
     * 寻找无参数构造函数
     *
     * @param type
     * @return
     */
    public static Constructor<?> findNoneParameterConstructor(Class<?> type) {
        assert (type != null);
        Constructor<?>[] constructors = type.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (getParameterCount(constructor) == 0) {
                return constructor;
            }
        }
        return null;
    }

    public static boolean supportParamterCount;

    static {

        try {
            Constructor.class.getMethod("getParameterCount");
            supportParamterCount = true;
        } catch (NoSuchMethodException e) {
            supportParamterCount = false;
        }


    }

    public static int getParameterCount(Constructor<?> constructor) {
        if (supportParamterCount) {
            return constructor.getParameterCount();
        } else {
            return constructor.getParameterTypes().length;
        }
    }

    public static int getParameterCount(Method method) {
        if (supportParamterCount) {
            return method.getParameterCount();
        } else {
            return method.getParameterTypes().length;
        }
    }


    public static String[] getClassPathes(){
        String str = System.getProperty("java.class.path");
        String[] parts = str.split(System.getProperty("path.separator"));
        return parts;
    }
}
