package com.jzoom.zoom.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于Controller的方法，表示渲染的模板在哪里，用哪个引擎
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Template {
	
	/**
	 * 默认模板路径,如果controller的方法返回{@link com.jzoom.zoom.web.view.TemplateView}仍然可以修改模板位置
	 * @return
	 */
	String path() default "";
	
	/**
	 * 引擎名称 
	 * @see com.jzoom.zoom.web.rendering.TemplateEngineManager
	 * @return
	 */
	String engine() default "";
	
}
