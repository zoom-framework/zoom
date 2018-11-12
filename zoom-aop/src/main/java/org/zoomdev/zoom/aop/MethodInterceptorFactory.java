package org.zoomdev.zoom.aop;

import org.zoomdev.zoom.aop.impl.AstractMethodInterceptorFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 对方法创建 {@link MethodInterceptor}
 *
 * @author jzoom
 * @see AstractMethodInterceptorFactory
 */
public interface MethodInterceptorFactory {
    /**
     * aop增强实际上是针对method增强
     *
     * @param targetClass  目标class
     * @param method       目标method
     * @param interceptors 这个数组将保存创建的AopMaker
     */
    void createMethodInterceptors(Class<?> targetClass, Method method, List<MethodInterceptor> interceptors);
}
