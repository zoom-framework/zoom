package org.zoomdev.zoom.dao.transaction;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;

import java.lang.reflect.Method;
import java.util.List;

public class TransMethodInterceptorMaker extends AnnotationMethodInterceptorFactory<Trans> {

	@Override
	protected void createMethodInterceptors(Trans annotation, Method method, List<MethodInterceptor> interceptors) {
		interceptors.add(new TransMethodInterceptor(annotation.level()));
	}

}
