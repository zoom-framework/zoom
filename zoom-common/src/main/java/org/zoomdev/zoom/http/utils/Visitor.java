package org.zoomdev.zoom.http.utils;


/**
 * 访问者模式
 *
 * @param <T>
 * @author jzoom
 */
public interface Visitor<T> {

    void visit(T data);
}
