package org.zoomdev.zoom.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Param {
	
	/**
	 * 如果参数的名称是这个值，表示的是RequestBody
	 */
	public static final String BODY = "@";
	
	/**
	 * 是否是整个body
	 * @return
	 */
	boolean body() default false;
	
	/**
	 * 是否是url参数
	 * @return
	 */
	boolean pathVariable() default false;

	/**
	 * 参数名字，如果有，替换掉函数参数名称
	 * @return
	 */
	String name() default "";

	/**
	 * 注释
	 * @return
	 */
	String comment() default "";
	
	
	
}
