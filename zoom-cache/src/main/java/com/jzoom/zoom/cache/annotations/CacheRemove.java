package com.jzoom.zoom.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheRemove {

	/**
	 * @see {Cache.name}
	 * @return
	 */
	String format() default "";
}
