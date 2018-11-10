package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.common.expression.Symbol;

import java.util.List;

/**
 * Entity active record
 */
public interface EAr<T> {


    /**
     * 对字段进行筛选,用于select/update/insert
     * @param filter
     * @return
     */
    EAr<T> filter(String filter);


    EAr<T> ignoreNull(boolean value);


    /**
     * 指定表
     * @param arr
     * @return
     */
    EAr<T> tables(String[] arr);

    /**
     * 指定表名称,这个选项在参数为实体对象的时候依然有效
     * @param table
     * @return
     */
    EAr<T> table(String table);


    /**
     * 凡是符合条件的都返回，需要确定返回的数据数量是少量的，否则引起程序运行慢
     * @return
     */
    List<T> find();


    List<T> limit(int position,int pageSize);


    Page<T> position(int position,int pageSize);


    Page<T> page(int page,int pageSize);


    /**
     * 在设置了数据的情况下
     * @return
     */
    int update(T data);

    /**
     * dao.ar().table("xxx").set("id",1).set("name","123").insertOrUpdate("id"),当id存在则更新，否则插入
     * @param keys
     * @return
     */
    int insertOrUpdate(String...keys);


    T get();

    /**
     * 根据主键查询,可以有多主键的情况,必须指定和主键相同个数的值
     * @param values
     * @return
     */
    T get(Object...values);

    /**
     * 插入一个实体对象或者Record
     * 实体对象不必实现注册，但是最好开启启动注册检查
     * @param data
     * @return
     */
    int insert(T data);

    /**
     * 批量插入
     * @param it
     * @return
     */
    int insert(Iterable<T> it);

    /**
     * 批量更新
     * @param it
     * @return
     */
    int update(Iterable<T> it);


    int delete(Iterable<T> it);
    /**
     *
     * 删除实体
     * @return
     */
    int delete(T data);

    /**
     * 查询结果数量
     * @return
     */
    int getCount();
    //######################################################
    // 查询相关方法
    //######################################################

    EAr<T> where(String field,Object value);

    EAr<T> orderBy(String field, SqlBuilder.Sort sort);


    EAr<T> join(String table,String on);

    EAr<T> orWhere(String field, Object value);

    <E> EAr<T> whereIn(String field, E... values);

    EAr<T> like(String name, SqlBuilder.Like like, Object value);

    EAr<T> whereCondition(String field,Object...values);

    EAr<T> where(String field, Symbol symbol, Object value);


}