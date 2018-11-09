package org.zoomdev.zoom.dao.adapters;

import java.sql.ResultSet;

/**
 * 将一个ResultSet转成一个其他的更加容易使用的类,如Record/实体类
 * @author jzoom
 *
 * @param <T>
 */
public interface ResultSetAdapter<T> {
	T adapter( ResultSet rs );
}
