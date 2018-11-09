package com.jzoom.zoom.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个Bean，可用于方法
 * @author jzoom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IocBean {
	
	/**
	 * ioc容器中的名称
	 * @return
	 */
	String name() default "";
	
	/**
	 * 在ioc中取出的时候调用的方法,在生命周期中只会调用一次
	 * 注意本标注与Method的Inject调用时机不同，Inject会在创建类的时候调用，而init是在第一次取出ioc容器的时候调用
	 * @return
	 */
	String initialize() default "";
	
	/**
	 * Bean的销毁方法，由ioc容器调用,在生命周期中只会调用一次
	 * @return
	 */
	String destroy() default "";
}
