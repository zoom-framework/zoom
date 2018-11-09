package org.zoomdev.zoom.web.parameter.adapter;


import org.zoomdev.zoom.web.action.ActionContext;

/**
 * 方法参数适配器
 * @author jzoom
 *
 * @param <T>
 */
public interface ParameterAdapter<T> {
	/**
	 * 
	 * @param context		context
	 * @param data			数据 (Map或者 HttpServletRequest)
	 * @param name			参数名称
	 * @param type			类型
	 * @return
	 */
	Object get(ActionContext context,T data,String name,Class<?> type);
}
