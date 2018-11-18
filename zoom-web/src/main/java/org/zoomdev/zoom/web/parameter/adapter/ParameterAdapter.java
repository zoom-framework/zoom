package org.zoomdev.zoom.web.parameter.adapter;


import org.zoomdev.zoom.web.action.ActionContext;

import java.lang.reflect.Type;

/**
 * 方法参数适配器
 *
 * @param <T>
 * @author jzoom
 */
public interface ParameterAdapter<T> {
    /**
     * @param context context
     * @param data    数据 (Map或者 HttpServletRequest)
     * @param name    参数名称
     * @param type    参数类型
     * @return
     */
    Object get(ActionContext context, T data, String name, Type type);
}
