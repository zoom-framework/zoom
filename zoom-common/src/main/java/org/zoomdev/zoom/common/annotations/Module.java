package org.zoomdev.zoom.common.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 表示一个模块,相当于spring boot中的configuration
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Module {
	
	/**
	 * 如果指定了其他的Annotation,表示需要这个Annotation指定才能启用这个功能
	 * @return
	 */
	Class<? extends Annotation> value() default Module.class;
	
	
}
