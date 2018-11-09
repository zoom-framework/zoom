package com.jzoom.zoom.aop.impl;

import java.lang.reflect.Method;

import com.jzoom.zoom.aop.MethodCaller;
import com.jzoom.zoom.common.Destroyable;

public class ReflectMethodCaller implements MethodCaller,Destroyable {
	
	private Method method;
	
	public ReflectMethodCaller(Method method) {
		this.method = method;
	}

	@Override
	public Object invoke(Object target, Object[] args) throws Exception {
		return method.invoke(target, args);
	}

	@Override
	public void destroy() {
		this.method = null;
	}

}
