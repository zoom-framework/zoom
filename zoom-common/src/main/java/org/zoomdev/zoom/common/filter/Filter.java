package org.zoomdev.zoom.common.filter;

/**
 * 搜索条件筛选器
 * <p>
 * 一般用于遍历所有要搜索的内容，如果满足条件，那么就accept返回true
 *
 * @param <T>
 * @author jzoom
 */
public interface Filter<T> {

    /**
     * 满足条件，返回true
     *
     * @param value
     * @return
     */
    boolean accept(T value);
}
