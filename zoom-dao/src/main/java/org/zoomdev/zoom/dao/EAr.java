package org.zoomdev.zoom.dao;

import org.apache.commons.lang3.ObjectUtils;
import org.zoomdev.zoom.common.utils.Page;

import java.util.List;

/**
 * Entity active record
 */
public interface EAr<T> extends Sql<EAr<T>> {


    /**
     * 对字段进行筛选,用于select/update/insert
     *
     * @param filter
     * @return
     */
    EAr<T> filter(String filter);


    /**
     * 针对插入和更新有效
     *
     * @param value
     * @return
     */
    EAr<T> ignoreNull(boolean value);


    /**
     * 凡是符合条件的都返回，需要确定返回的数据数量是少量的，否则引起程序运行慢
     *
     * @return
     */
    List<T> find();


    List<T> limit(int position, int size);


    Page<T> position(int position, int size);


    Page<T> page(int page, int size);


    /**
     * 在设置了数据的情况下
     *
     * @return
     */
    int update(T data);



    T get();

    /**
     * 根据主键查询,可以有多主键的情况,必须指定和主键相同个数的值
     *
     * @param values
     * @return
     */
    T get(Object... values);

    /**
     * 插入一个实体对象或者Record
     * 实体对象不必实现注册，但是最好开启启动注册检查
     *
     * @param data
     * @return
     */
    int insert(T data);



    /**
     * 删除实体
     *
     * @return
     */
    int delete(T data);

    /**
     * 获取记录数量           dao.ar(Book.class).count();/dar.ar("book").count()
     *
     * @return
     */
    int count();

    /**
     * 直接获取值            dao.ar(Book.class).where("id",1).getValue("id",String.class)
     *
     * @param key
     * @param typeOfE
     * @param <E>
     * @return
     */
    <E> E value(String key, Class<E> typeOfE);

    EAr<T> setEntity(Entity entity);


    Entity getEntity();

    /**
     * 是否是严格模式，
     * 在严格模式下，update和insert每一个字段都会被检查，如果不存在则会报错
     * 而在非严格模式下，如果字段不存在，则会被过滤掉
     *
     * 默认情况下是严格模式
     *
     * @param strict
     * @return
     */
    EAr<T> strict(boolean strict);


    int delete();

    /**
     * 批量插入
     *
     * @param it
     * @return
     */
    int insert(Iterable<T> it);

    /**
     * 批量更新
     *
     * @param it
     * @return
     */
    int update(Iterable<T> it);

    int delete(Iterable<T> it);

}
