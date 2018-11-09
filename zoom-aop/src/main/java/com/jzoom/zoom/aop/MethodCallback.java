package com.jzoom.zoom.aop;

import java.lang.reflect.Method;

public class MethodCallback implements MethodInterceptor{
	protected void before(Method method,Object[] args) {
	}
	
	protected void error(Method method,Object[] args,Throwable throwable) {
	}
	
	protected void after(Method method,Object[] args,Object result) {
	}
	
	protected void complete(Method method) {
	}

	@Override
	public void intercept(MethodInvoker invoker) throws Throwable {
		before(invoker.getMethod(), invoker.getArgs());
		try {
			invoker.invoke();
			after(invoker.getMethod(), invoker.getArgs(), invoker.getReturnObject());
		}catch (Throwable e) {
			error(invoker.getMethod(), invoker.getArgs(), e);
			throw e;
		}finally {
			complete(invoker.getMethod());
		}
	}
}
