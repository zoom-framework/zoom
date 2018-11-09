package org.zoomdev.zoom.aop.reflect;

import java.lang.reflect.Method;

/**
 * 获取class的元数据
 * @author jzoom
 *
 */
public interface ClassInfo {
	
	/**
	 * 获取一个方法的参数名称
	 * @param clazz
	 * @param method
	 * @return
	 */
	String[] getParameterNames(Class<?> clazz, Method method );
}
