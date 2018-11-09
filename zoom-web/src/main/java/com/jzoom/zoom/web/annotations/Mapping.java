package com.jzoom.zoom.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mapping {

	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String OPTIONS = "OPTIONS";
	public static final String[] ALL = new String[] { GET, POST, PUT, DELETE,OPTIONS };
	

	/**
	 * 路由的路径,  这个只能用于方法，与Spring不同, 具体结果为
	 * {@link Controller#key} + "/" + name
	 * 
	 * 
	 * @see Controller
	 * 
	 * 
	 * @return
	 */
	String value();

	/**
	 * 默认不限制method
	 * 
	 * @return
	 */
	String[] method() default {GET, POST};

}
