package com.jzoom.zoom.cache;

import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import com.jzoom.zoom.cache.annotations.Cache;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class CacheGetInterceptorFactory extends AnnotationMethodInterceptorFactory<Cache> {

	private DataCache cache;
	
	public CacheGetInterceptorFactory(DataCache cache){
		this.cache = cache;
	}



	@Override
	protected void createMethodInterceptors(Cache annotation, Method method, List<MethodInterceptor> interceptors) {
		String format = annotation.format();
		int count = method.getParameterTypes().length;
		if(StringUtils.isEmpty(format)) {
			format = method.toString() + ":"+ StringUtils.join(Collections.nCopies( count,"%s"),":");
		}else {
			int formatCount = StringUtils.countMatches(format, "%s");
			if (formatCount > count) {
				throw new RuntimeException("%s的个数不能大于参数个数");
			}
		}
		int timeoutMs= annotation.timeoutSeconds() * 1000;
		boolean fill = annotation.fill();
		interceptors.add(new CacheGetInterceptor(format, count, cache, fill, timeoutMs, annotation.ignoreNull(), annotation.lockWhenNull()));
		
		
	}

}
