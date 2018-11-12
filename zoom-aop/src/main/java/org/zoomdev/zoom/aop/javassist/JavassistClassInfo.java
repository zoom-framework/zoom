package org.zoomdev.zoom.aop.javassist;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.aop.utils.JavassistUtils;

import java.lang.reflect.Method;

public class JavassistClassInfo implements ClassInfo {

    static final CtClass objectType;

    static {
        try {
            objectType = ClassPool.getDefault().get("java.lang.Object");
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getParameterNames(Class<?> clazz, Method method) {
        assert (clazz != null && method != null);
        try {
            ClassPool pool = JavassistUtils.getClassPool();
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
                    throw new RuntimeException("名称为null");
                }
                names[i] = name;
            }
            return names;
        } catch (Exception e) {
            throw new RuntimeException("获取方法参数名称失败" + method, e);
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
