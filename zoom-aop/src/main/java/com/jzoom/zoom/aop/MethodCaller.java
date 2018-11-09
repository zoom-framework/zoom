package com.jzoom.zoom.aop;

public interface MethodCaller {

	Object invoke(Object target,Object [] args) throws Exception;
	
}
