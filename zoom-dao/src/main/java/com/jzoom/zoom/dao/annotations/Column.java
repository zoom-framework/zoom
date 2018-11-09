package com.jzoom.zoom.dao.annotations;

import com.jzoom.zoom.dao.adapters.DataAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 标注对应的是数据库的那个字段（或者是select语句中的部分）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    /**
     * 数据库字段名称，或者select(*)等函数运算或者任意数据库表达式
     * 如果有其他表的字段，则默认情况下会查找 {@link Join} 标注的其他表
     * @return
     */
    String value() default "";

    /**
     * 数据适配器
     *
     * @return
     */
    Class<? extends DataAdapter> adapter() default DataAdapter.class;

}
