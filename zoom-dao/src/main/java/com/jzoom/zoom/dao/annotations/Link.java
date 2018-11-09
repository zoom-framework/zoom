package com.jzoom.zoom.dao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Link {

    /**
     * 其他表中取出数据，其他表的字段有哪些与本表的主键是一一对应的
     *
     * 比如:
     *
     * @Table("type")
     * // 表 type
     * class Type{
     * @Link(columns={"typeId"})        标注了Link表示这个字段从其他表中来  一对多的关系
     *     List<Product> products;
     *
     * @Link(columns="extraId")         一对一的关系
     *     TypeExtra extra;
     * }
     *
     * @Table("product")
     * //表 product
     * class Product{
     *  //分类id
     *  String typeId;
     *
     * }
     *
     *
     *
     *
     * @return
     */
    String[] columns();

}
