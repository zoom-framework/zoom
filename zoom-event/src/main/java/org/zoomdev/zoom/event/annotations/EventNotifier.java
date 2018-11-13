package org.zoomdev.zoom.event.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示这个方法返回的值，
 * 会调用EventService.notifyObservers(name,returnValue)
 * name 为标注指定的value
 * returnValue 为方法返回值
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventNotifier {

    String value();

}
