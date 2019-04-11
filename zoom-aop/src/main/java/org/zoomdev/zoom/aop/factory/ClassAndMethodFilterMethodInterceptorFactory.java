package org.zoomdev.zoom.aop.factory;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInterceptorFactory;
import org.zoomdev.zoom.http.filter.ClassAndMethodFilter;
import org.zoomdev.zoom.http.filter.impl.PatternClassAndMethodFilter;

import java.lang.reflect.Method;
import java.util.List;

public class ClassAndMethodFilterMethodInterceptorFactory implements MethodInterceptorFactory {


    private ClassAndMethodFilter filter;
    private MethodInterceptor interceptor;

    public ClassAndMethodFilterMethodInterceptorFactory(ClassAndMethodFilter filter, MethodInterceptor interceptor) {
        this.filter = filter;
        this.interceptor = interceptor;
    }

    public ClassAndMethodFilterMethodInterceptorFactory(String pattern, MethodInterceptor interceptor) {
        this(new PatternClassAndMethodFilter(pattern), interceptor);
    }


    @Override
    public void createMethodInterceptors(Class<?> targetClass, Method method, List<MethodInterceptor> interceptors) {
        if (filter.accept(targetClass) && filter.accept(targetClass, method)) {
            interceptors.add(interceptor);
        }
    }

}
