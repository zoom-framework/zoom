package org.zoomdev.zoom.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 必须的参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Required {

    /**
     * 如果为null，则出现的提示
     *
     * @return
     */
    String value() default "";
}
