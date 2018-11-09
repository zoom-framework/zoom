package org.zoomdev.zoom.aop.factory;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;
import org.zoomdev.zoom.aop.annotations.Log;
import org.zoomdev.zoom.common.logger.Logger;
import org.zoomdev.zoom.common.logger.Loggers;
import org.zoomdev.zoom.common.utils.Classes;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

public class LogMethodInterceptorFactory extends AnnotationMethodInterceptorFactory<Log> {

	public static class MethodLogInterceptor implements MethodInterceptor{

		private Logger Logger = Loggers.getLogger();
		
		@Override
		public void intercept(MethodInvoker invoker) throws Throwable {
			try {
				invoker.invoke();
				Logger.info("目标对象[%s] 方法[%s] 参数[%s] 执行结果[%s]", invoker.getTarget(), invoker.getMethod(),StringUtils.join(invoker.getArgs(),",") ,invoker.getReturnObject());
			}catch (Throwable e) {
				Logger.error(e, "目标对象[%s] 方法[%s] 参数[%s] 执行发生异常[%s]", invoker.getTarget(), invoker.getMethod(), StringUtils.join(invoker.getArgs(), ","),
						e);
                throw Classes.getCause(e);
			}
			
		}
		
	}
	
	MethodLogInterceptor defaultInterceptor = new MethodLogInterceptor();
	
	
	@Override
	protected void createMethodInterceptors(Log annotation, Method method, List<MethodInterceptor> interceptors) {
		interceptors.add(  defaultInterceptor );
	}

}
