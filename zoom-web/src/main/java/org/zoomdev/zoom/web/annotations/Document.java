package org.zoomdev.zoom.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 表示一个文档说明，可以用于标注了 {@link Controller} 的控制器上面，为本控制器增加说明
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
public @interface Document {
	
	/**
	 * 名字
	 * @return
	 */
	String name() default "";

	/**
	 * 注释
	 * @return
	 */
	String comment() default "";
	
}
