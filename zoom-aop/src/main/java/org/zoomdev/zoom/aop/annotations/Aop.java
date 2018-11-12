package org.zoomdev.zoom.aop.annotations;

import org.zoomdev.zoom.aop.MethodInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Aop {

    /**
     * 指定切面
     *
     * @return
     */
    Class<? extends MethodInterceptor>[] value() default {};

}
