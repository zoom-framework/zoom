package org.zoomdev.zoom.aop.javassist;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.StreamClassLoader;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JavassistClassInfo implements ClassInfo {

    static final CtClass objectType;
    private final ClassPool classPool;


    public JavassistClassInfo(ClassPool classPool) {
        this.classPool = classPool;
    }

    static {
        try {
            objectType = ClassPool.getDefault().get("java.lang.Object");
        } catch (NotFoundException e) {
            throw new ZoomException(e);
        }
    }

    static class StreamClassPath implements ClassPath {
        private StreamClassLoader classLoader;

        public StreamClassPath(StreamClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        public InputStream openClassfile(String classname) throws NotFoundException {
            return classLoader.getStream(classname);
        }

        @Override
        public URL find(String classname) {
            return classLoader.getUrl(classname);
        }

        @Override
        public void close() {

        }
    }

    public String[] getParameterNames(Class<?> clazz, Method method) {
        assert (clazz != null && method != null);
        try {
            ClassPool pool = classPool;
            CtClass cc;
            try {
                cc = pool.get(clazz.getName());
            } catch (NotFoundException e) {
                pool.appendClassPath(new ClassClassPath(clazz));
                cc = pool.get(clazz.getName());
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            int len = paramTypes.length;
            String[] names = new String[len];
            if (len == 0) {
                return names;
            }
            CtClass[] types = new CtClass[len];
            for (int i = 0; i < len; ++i) {
                types[i] = pool.get(paramTypes[i].getName());
            }
            CtMethod cm = getDeclaredMethod(cc, method.getName(), types);
            if (cm == null) {
                return names;
            }
            // 使用javaassist的反射方法获取方法的参数名
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            for (int i = 0; i < len; i++) {
                String name = attr.variableName(i + pos);
                if (name == null) {
                    throw new ZoomException("名称为null");
                }
                names[i] = name;
            }
            return names;
        } catch (Exception e) {
            throw new ZoomException("获取方法参数名称失败" + method, e);
        }
    }


    private List<StreamClassPath> paths = new ArrayList<StreamClassPath>();

    @Override
    public synchronized void appendClassLoader(StreamClassLoader classLoader) {
        ClassPool pool = classPool;
        StreamClassPath classPath = new StreamClassPath(classLoader);
        paths.add(classPath);
        pool.insertClassPath(classPath);
    }

    @Override
    public synchronized void removeClassLoader(StreamClassLoader classLoader) {
        for (int i = 0, c = paths.size(); i < c; ++i) {
            StreamClassPath p = paths.get(i);
            if (p.classLoader == classLoader) {
                paths.remove(p);
                ClassPool pool = classPool;
                pool.removeClassPath(p);
                break;
            }
        }
    }

    private static CtMethod getDeclaredMethod(CtClass cc, String name, CtClass[] types) throws Exception {
        do {
            try {
                return cc.getDeclaredMethod(name, types);
            } catch (NotFoundException e) {
                cc = cc.getSuperclass();
            }

        } while (cc != objectType);
        return null;
    }

}
