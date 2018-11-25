package org.zoomdev.zoom.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个Bean，可用于方法
 *
 * @author jzoom
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface IocBean {

    /// order = SYSTEM , 表示依赖本IocBean指定Bean的方法将会优先执行。
    int SYSTEM = 0;

    /// order = USER, 表示会对方法或者字段注入无要求。
    int USER = 1;


    /**
     * ioc容器中的名称
     *
     * @return
     */
    String name() default "";

    /**
     * 在ioc中取出的时候调用的方法,在生命周期中只会调用一次
     * 注意本标注与Method的Inject调用时机不同，Inject会在创建类的时候调用，而init是在第一次取出ioc容器的时候调用
     *
     * @return
     */
    String initialize() default "";

    /**
     * Bean的销毁方法，由ioc容器调用,在生命周期中只会调用一次
     *
     * @return
     */
    String destroy() default "";

    /**
     * {@link IocBean#USER}
     * {@link IocBean#SYSTEM}
     *
     * @return
     */
    int order() default USER;
}
