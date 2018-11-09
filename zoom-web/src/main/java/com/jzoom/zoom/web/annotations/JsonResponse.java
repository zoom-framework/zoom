package com.jzoom.zoom.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jzoom.zoom.web.resp.JsonResponseAdapter;

/**
 * 
 * 表示输出json
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface JsonResponse {
	
	/**
	 * json数据适配器
	 * @return
	 */
	Class<? extends JsonResponseAdapter> value() default JsonResponseAdapter.class;
	
}
