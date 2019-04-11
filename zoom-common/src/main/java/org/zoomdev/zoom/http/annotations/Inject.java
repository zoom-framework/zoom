package org.zoomdev.zoom.http.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Inject {


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

    /**
     * {@link IocBean#USER}
     * {@link IocBean#SYSTEM}
     *
     * @return
     */
    int order() default IocBean.USER;

}
