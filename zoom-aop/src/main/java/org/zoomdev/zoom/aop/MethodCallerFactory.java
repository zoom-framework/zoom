package org.zoomdev.zoom.aop;

import java.lang.reflect.Method;

public interface MethodCallerFactory {
	MethodCaller create(Method method);
}
