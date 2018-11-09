package org.zoomdev.zoom.aop;

public interface MethodCaller {

	Object invoke(Object target,Object [] args) throws Exception;
	
}
