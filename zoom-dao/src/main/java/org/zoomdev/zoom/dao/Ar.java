package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.alias.NameAdapter;

import java.util.List;
import java.util.Map;

/**
 * ActiveRecord 接口
 * 别名系统： 也叫做逻辑名称，相对于数据库原始字段和表的物理名称
 * 实践证明：
 * 1、使用别名比原始名称要容易记忆，
 * 2、在一个系统中的别名是有限的   如常见的； name/title/thumb/image/bg/nick等
 * 3、找到一种方法，将表中的所有物理名称全部映射成为逻辑名称
 * 4、逻辑名称可用于网络传输、程序等
 * 5、自动生成逻辑名称的常量
 *
 * @author jzoom
 */
public interface Ar extends ConnectionHolder, Sql<Ar> {


    Ar nameAdapter(NameAdapter nameAdapter);

    List<Record> find();


    List<Record> limit(int position, int size);


    Page<Record> position(int position, int size);


    Page<Record> page(int page, int size);

    /**
     * @param sql
     * @param args
     * @return
     */
    List<Record> executeQuery(String sql, Object... args);

    /**
     * 更新记录
     *
     * @param record
     * @return
     */
    int update(Map<String, Object> record);

    /**
     * 在设置了数据的情况下
     *
     * @return
     */
    int update();


    Ar setAll(Map<String, Object> record);


    Ar set(String key, Object value);


    Record get();


    /**
     * 插入一个实体对象或者Record
     * 实体对象不必实现注册，但是最好开启启动注册检查
     *
     * @param data
     * @return
     */
    int insert(Map<String, Object> data);


    int insert();


    /**
     * @return
     */
    int delete();

    /**
     * 查询结果数量
     *
     * @return
     */
    int count();


    int executeUpdate(String sql, Object... args);


    int execute(String sql);

    <E> E value(final String key, final Class<E> typeOfE);

    Ar selectMax(String field);

    Ar selectSum(String field);


    /**
     * @param table
     * @return
     */
    Ar table(String table);


    /**
     * dao.ar().table("xxx").set("id",1).set("name","123").insertOrUpdate("id"),当id存在则更新，否则插入
     *
     * @param keys
     * @return
     */
    int insertOrUpdate(String... keys);


    Ar whereCondition(String key, Object... values);

    /**
     * @param type
     * @param generatedKeys 生成键
     * @return
     */
    int insert(Record type, String... generatedKeys);
}
