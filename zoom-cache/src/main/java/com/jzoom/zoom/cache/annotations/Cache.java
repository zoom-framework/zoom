package com.jzoom.zoom.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cache {

	/**
	 * 键值 格式为  mykey%s_%s  ，在调用的时候，
	 * 作为String.format的第一个参数，后面的参数列表为原函数参数列表，数量为 %s的个数，
	 * 所以要保证 %s的数量大于等于 参数数量
	 * 如果没有提供，那么默认为 class:method:%s%s...
	 * 
	 * @return
	 */
	String format() default "";
	
	
	/**
	 * 超时秒数
	 * @return
	 */
	int timeoutSeconds() default 20 * 60;
	
	/**
	 * 如果没有从缓存拿到结果，那么就将方法返回的结果放到缓存中
	 * @return
	 */
	boolean fill() default true;

	/**
	 * 是否忽略null值
	 * @return
	 */
	boolean ignoreNull() default true;

    /**
     * 如果缓存没有获取到值，那么锁定一下这个缓存对应的key
     *
     * @return
     */
    boolean lockWhenNull() default false;
	
}
