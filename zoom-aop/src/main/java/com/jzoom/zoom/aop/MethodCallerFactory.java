package com.jzoom.zoom.aop;

import java.lang.reflect.Method;

public interface MethodCallerFactory {
	MethodCaller create(Method method);
}
