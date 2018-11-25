package org.zoomdev.zoom.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Inject {
    /**
     *
     * 如果order设置为0，那么表示这个方法在所有ioc对象开始注入前注入
     *
     */
    public static final int SYSTEM= 0;


    public static final int USER = 1;

    /**
     * IoxBean的名臣
     *
     * @return
     */
    String value() default "";

    /**
     * 配置名称,需要改成 Config 标注
     *
     * @return
     */
    String config() default "";


    int order() default USER;

}
