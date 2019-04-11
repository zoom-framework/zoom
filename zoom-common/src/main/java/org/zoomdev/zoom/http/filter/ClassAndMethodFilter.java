package org.zoomdev.zoom.http.filter;

import java.lang.reflect.Method;

/**
 * 类+方法的过滤器
 */
public interface ClassAndMethodFilter {
    boolean accept(Class<?> clazz);

    boolean accept(Class<?> clazz, Method method);
}
