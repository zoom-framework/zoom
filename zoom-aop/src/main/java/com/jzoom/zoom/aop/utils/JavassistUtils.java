package com.jzoom.zoom.aop.utils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

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
