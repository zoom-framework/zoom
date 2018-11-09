package com.jzoom.zoom.aop.factory;

import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.annotations.Aop;

import java.lang.reflect.Method;
import java.util.List;

public class AopMethodInterceptorFactory extends AnnotationMethodInterceptorFactory<Aop> {

	
	@Override
	protected void createMethodInterceptors(Aop annotation, Method method, List<MethodInterceptor> interceptors) {
		
		Class<? extends MethodInterceptor>[] classOfMethodInterceptor=annotation.value();
		
		for (Class<? extends MethodInterceptor> clazz : classOfMethodInterceptor) {
			try {
				interceptors.add(clazz.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(String.format("在初始化MethodInterceptor[%s]的时候发生异常", clazz),e);
			} 
		}
	}

}
