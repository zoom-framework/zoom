package org.zoomdev.zoom.dao.annotations;

import org.zoomdev.zoom.dao.Ar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Join {

    /**
     * 表名称
     */
    String table();

    /**
     * 表名称的别名
     *
     * @return
     */
    String alias() default "";

    /**
     * 字符:[a-zA-Z0-9()><= ]
     *
     * @return
     */
    String on();

    String type() default Ar.INNER;
}
