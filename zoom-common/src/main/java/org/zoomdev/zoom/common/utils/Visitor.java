package org.zoomdev.zoom.common.utils;


/**
 * 访问者模式
 * @author jzoom
 *
 * @param <T>
 */
public interface Visitor<T> {

	void visit(T data);
}
