package com.jzoom.zoom.event.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法标注Event,表示对这个事件感兴趣,参数个数为 1个或者0个
 *
 * 如果第一个参数为Event,那么直接传入Event对象
 * 如果第一个参数非Event，那么使用Event.getData()传入参数
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventObserver {
    String value();
}
