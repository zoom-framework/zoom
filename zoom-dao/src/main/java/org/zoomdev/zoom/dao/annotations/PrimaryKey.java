package org.zoomdev.zoom.dao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注主键,
 * 只要一个实体类中有一个这样的标注，
 * 那么就直接忽略掉数据库中的真实主键,标注的主键，将用于
 * {@link org.zoomdev.zoom.dao.EAr#get(Object...)}
 * <p>
 * 如果一个实体类中不含有标注主键，那么就使用数据库中的主键
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
}
