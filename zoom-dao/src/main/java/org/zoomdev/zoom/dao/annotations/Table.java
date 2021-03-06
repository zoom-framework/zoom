package org.zoomdev.zoom.dao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * 指定这个实体类对应到哪张表
     *
     * @return
     */
    String value();

    /**
     * 表名称的别名
     *
     * @return
     */
    String alias() default "";
}
