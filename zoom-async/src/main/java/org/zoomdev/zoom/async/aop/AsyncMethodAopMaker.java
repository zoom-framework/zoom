package org.zoomdev.zoom.async.aop;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import org.zoomdev.zoom.async.annotation.Async;
import org.zoomdev.zoom.async.impl.Asyncs;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

public class AsyncMethodAopMaker extends AnnotationMethodInterceptorFactory<Async> {

	@Override
	protected void createMethodInterceptors(Async annotation, Method method, List<MethodInterceptor> interceptors) {
		interceptors.add(interceptor);
	}
	
	AsyncMethodInterceptor interceptor = new AsyncMethodInterceptor();
	
	private static class AsyncMethodInterceptor implements MethodInterceptor{

		@Override
		public void intercept(final MethodInvoker invoker) throws Throwable {
			
			Asyncs.defaultJobQueue().submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					try {
						invoker.invoke();
					} catch (Throwable e) {
						throw new RuntimeException("执行错误",e);
					}
					return null;
				}
			});
			
		}
		
	}

	

}
