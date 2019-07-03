package org.zoomdev.zoom.common.filter;

import java.lang.reflect.Method;

/**
 * 类+方法的过滤器
 * <p>
 * 注意底下两个过滤器必须同时成立
 */
public interface ClassAndMethodFilter {
    boolean accept(Class<?> clazz);

    boolean accept(Class<?> clazz, Method method);
}
