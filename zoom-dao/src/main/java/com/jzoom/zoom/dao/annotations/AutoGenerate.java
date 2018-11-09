package com.jzoom.zoom.dao.annotations;

import com.jzoom.zoom.dao.AutoGenerateValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 本字段自动生成,如果数据库不能读取出来，则需要提供
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoGenerate {

    /**
     * 序列名称,如果提供了，表示由sequence生成id
     * 在插入的时候自动调用  insert into xx (aa) values (select sequence_name.next_val() from dual)
     * @return
     */
    String sequence() default "";

    /**
     * 如果有的话，直接生成
     * @return
     */
    Class<? extends AutoGenerateValue> factory() default AutoGenerateValue.class;
}
