package com.jzoom.zoom.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定一个 {@link com.jzoom.zoom.web.action.ActionFactory}
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface ActionFactory {
	
	Class<? extends com.jzoom.zoom.web.action.ActionFactory> value();
	
}
