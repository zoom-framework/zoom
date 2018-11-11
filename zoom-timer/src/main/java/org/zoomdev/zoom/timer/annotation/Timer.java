package org.zoomdev.zoom.timer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对一个Method标注，表示一个定时任务
 * @author randyren
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timer {
	
	/**
	 * cron表达式, 可以指定配置 ${config_name}
	 * @return
	 */
    String value() default "";

    /**
     * 指定every之后，自动生成corn表达式
     *
     * @return
     */
    Every every() default Every.NONE;

    /**
     * 指定every之后有效 如: Every.SECOND,everyValue 1 :    0/1 * * * * ?
     *
     * @return
     */
    int everyValue() default 1;

    enum Every {
        NONE,
        SECOND,
        MINUTE,
        HOUR
    }
}
