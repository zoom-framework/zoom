package org.zoomdev.zoom.dao.factory;

import org.zoomdev.zoom.http.utils.CachedClasses;
import org.zoomdev.zoom.dao.DaoException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DaoModelFactory {

    private List<DaoParameterFactory> factoryList = new ArrayList<DaoParameterFactory>();

    public void addFactory() {

    }


    private DaoInvoker create(Method method) {

        Class<?>[] types = method.getParameterTypes();
        DaoParameter[] parameters = new DaoParameter[types.length];
        Type returnType = method.getGenericReturnType();
        Annotation[][] methodParameterAnnotations = method.getParameterAnnotations();
        int index = 0;
        for (DaoParameterFactory factory : factoryList) {
            Annotation[] annotations = methodParameterAnnotations[index];
            DaoParameter parameter = null;
            for (Annotation annotation : annotations) {
                if (factory.isAnnotation(annotation)) {
                    parameter = factory.create(types[index], annotation);
                    parameters[index] = parameter;
                }
            }

            //有个参数不能创建?
            if (parameter == null) {
                throw new DaoException("参数不能创建适配器");
            }

            ++index;
        }

        return null;
    }

    public Object create(Class<?> interfaceClass) {

        if (!interfaceClass.isInterface()) {
            throw new DaoException("必须是接口才能实现代理");
        }

        Method[] methods = CachedClasses.getPublicMethods(interfaceClass);


        for (Method method : methods) {

        }


        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new DaoProxyHandler()
        );
    }


}
