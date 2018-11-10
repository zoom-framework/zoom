package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.dao.SqlBuilder.Like;
import org.zoomdev.zoom.dao.SqlBuilder.Sort;

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
 * @author jzoom
 *
 */
public interface Ar extends Trans,ConnectionHolder  {

	Ar tables(String[] arr);
	/**
	 * 指定表名称,这个选项在参数为实体对象的时候依然有效
	 * @param table
	 * @return
	 */
	Ar table(String table);

	/**
	 * 凡是符合条件的都返回，需要确定返回的数据数量是少量的，否则引起程序运行慢
	 * @return
	 */
	List<Record> find();


	List<Record> limit(int position,int pageSize);


	Page<Record> position(int position,int pageSize);


	Page<Record> page(int page,int pageSize);

	/**
	 * @param sql
	 * @param args
	 * @return
	 */
	List<Record> executeQuery(String sql,Object...args);
	/**
	 * 更新记录
	 * @param record
	 * @return
	 */
	int update(Map<String, Object> record);
	
	/**
	 * 在设置了数据的情况下
	 * @return
	 */
	int update();
	
	/**
	 * dao.ar().table("xxx").set("id",1).set("name","123").insertOrUpdate("id"),当id存在则更新，否则插入 
	 * @param keys
	 * @return
	 */
	int insertOrUpdate(String...keys);


	Ar setAll(Map<String, Object> record);
	
	
	Ar set(String key,Object value);
	

	Record get();

	/**
	 * 获取一个值
	 * @param select
	 * @param classOfT
	 * @param <T>
	 * @return
	 */
	<T> T getValue(String select,Class<T> classOfT);

	/**
	 * 插入一个实体对象或者Record
	 * 实体对象不必实现注册，但是最好开启启动注册检查
	 * @param data
	 * @return
	 */
	int insert(Map<String,Object> data);


	int insert();
	



	<T> T execute(ConnectionExecutor executor);

	/**
	 *
	 * @return
	 */
	int delete();

	/**
	 * 查询结果数量
	 * @return
	 */
	int getCount();
	//######################################################
	// 查询相关方法
	//######################################################
	
	Ar where(String key,Object value);

	Ar orderBy(String field, Sort sort);

	Ar select(String select);

	Ar select(Iterable<String> select);

	Ar join(String table,String on);

    Ar join(String table, String on, String type);

	Ar orWhere(String key, Object value);

	<T> Ar whereIn(String key, T... values);

	Ar like(String name,Like like,Object value);
	
	Ar whereCondition(String key,Object...values);

	Ar where(String key, Symbol symbol, Object value);


	Ar selectMax(String field);


	int executeUpdate(String sql, Object... args);
}