package com.jzoom.zoom.dao;

/**
 * 对于本接口来说，会有特殊处理
 *
 * interface ADaoModel extends DaoModel\<A\>
 * {
 *     List<A> find(
 *        @Like
 *        String title);
 * }
 *
 *
 */
public interface DaoModel<T> {

}
