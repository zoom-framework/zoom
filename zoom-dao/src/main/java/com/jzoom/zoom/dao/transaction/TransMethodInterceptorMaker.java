package com.jzoom.zoom.dao.transaction;

import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.factory.AnnotationMethodInterceptorFactory;

import java.lang.reflect.Method;
import java.util.List;

public class TransMethodInterceptorMaker extends AnnotationMethodInterceptorFactory<Trans> {

	@Override
	protected void createMethodInterceptors(Trans annotation, Method method, List<MethodInterceptor> interceptors) {
		interceptors.add(new TransMethodInterceptor(annotation.level()));
	}

}
