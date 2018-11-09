package org.zoomdev.zoom.aop.utils;

import javassist.ClassPool;

public class JavassistUtils {
	
	private static ClassPool pool;

	
	public static ClassPool getClassPool() {
		if(pool == null) {
			synchronized (JavassistUtils.class) {
				pool = new ClassPool(ClassPool.getDefault());
			}
		}
		return pool;
	}
	
	public static void destroy() {
		pool = null;
	}

}
