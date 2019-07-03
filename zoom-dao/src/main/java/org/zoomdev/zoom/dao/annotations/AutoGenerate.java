package org.zoomdev.zoom.dao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动生成主键
 * <p>
 * 注意其他字段设置了这个标注插入会报错。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoGenerate {

    /**
     * 序列名称,如果提供了，表示由sequence生成id
     * 在插入的时候自动调用
     * insert into xx (aa) values (select sequence_name.next_val() from dual)
     *
     * @return
     */
    String sequence() default "";


}
