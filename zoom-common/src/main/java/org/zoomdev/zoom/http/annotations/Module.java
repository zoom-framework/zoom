package org.zoomdev.zoom.http.annotations;

import java.lang.annotation.*;

/**
 * 表示一个模块,相当于spring boot中的configuration
 *
 * @author jzoom
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Module {

    /**
     * 如果指定了其他的Annotation,表示需要这个Annotation指定才能启用这个功能
     *
     * @return
     */
    Class<? extends Annotation> value() default Module.class;


}
