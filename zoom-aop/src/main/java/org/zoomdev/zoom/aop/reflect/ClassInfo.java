package org.zoomdev.zoom.aop.reflect;

import org.zoomdev.zoom.common.utils.StreamClassLoader;

import java.lang.reflect.Method;

/**
 * 获取class的元数据
 *
 * @author jzoom
 */
public interface ClassInfo {

    /**
     * 获取一个方法的参数名称
     *
     * @param clazz
     * @param method
     * @return
     */
    String[] getParameterNames(Class<?> clazz, Method method);

    /**
     * 通过类名称获取到stream
     *
     * @param classLoader
     */
    void appendClassLoader(StreamClassLoader classLoader);

    /**
     * 移除
     *
     * @param classLoader
     */
    void removeClassLoader(StreamClassLoader classLoader);
}
