package org.zoomdev.zoom.dao.factory;

import org.zoomdev.zoom.dao.EAr;

/**
 * 最后执行以下返回,可能情况为
 * Page
 * List
 * int
 * Object/Record/instance
 *
 * @param <T>
 */
public interface DaoReturn<T> {

    T get(EAr<T> ar);
}
