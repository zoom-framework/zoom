package com.jzoom.zoom.web.action;

import java.lang.reflect.Method;

import com.jzoom.zoom.common.filter.ClassAndMethodFilter;

public interface ActionInterceptorFactory {

	/**
	 * 注册interception
	 * @param interceptor 
	 * @param pattern 类、方法过滤，形式{@code *} {@code *#*} {@code com.xx.*#*Test} {@link com.jzoom.zoom.common.filter.impl#ClassAndMethodFilter }
	 */
	void add( ActionInterceptor interceptor, String pattern ,int order );
	
	/**
	 * 
	 * @param interceptor
	 * @param pattern
	 * @param order
	 */
	void add( ActionInterceptor interceptor, ClassAndMethodFilter filter ,int order );
	/**
	 * 根据控制器class和方法创建ActionInterceptor
	 * @param controllerClass
	 * @param method
	 * @return
	 */
	ActionInterceptor[] create(Class<?> controllerClass, Method method);
	
}
