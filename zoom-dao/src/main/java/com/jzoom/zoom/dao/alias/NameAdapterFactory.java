package com.jzoom.zoom.dao.alias;

import com.jzoom.zoom.dao.adapters.NameAdapter;
/**
 * 对字段的重命名策略
 * 
 * # 为什么要用别名?
 * 
 * 一般来说，java的命名是驼峰式，而数据库里面的命名方式一般是下划线式，很多情况下需要增加前缀
 * 如：
 * 
 * 表 s_product    s_前缀
 * 其中字段 (
 * 		pro_id int,tp_id int,pro_title varchar2,pro_price number,
 * ) 	pro_为本表字段前缀，tp_ 为type表前缀，那么写成实体类:
 * 
 * class Product{
 *   int id;
 *   int tpId;
 *   String title;
 *   double price;
 * }
 * 
 * 需要将数据库的命名转成这里的java习惯的命名
 * 
 * # 怎样别名？
 * 
 * 策略： 自动识别字段前缀，对于不符合本表前缀的字段，自动改成驼峰式命名
 * 
 * 对于表：
 * 可能存在如下命名：
 * 
 * s_product
 * s_type
 * s_customer
 * s_order
 * ...
 * 表示商城的业务需求，都用s_表示，那么其他的业务中可能也存在相同意义的表
 * 
 * r_order
 * r_customer
 * r_product
 * r_type
 * 
 * 表示一个其他的业务，
 * 在同一个系统中的表基本没有办法自动识别前置进行重命名了，
 * 可以将表的别名放在实体类的Annotation进行处理，配置文件中或配置项中进行映射，
 * 我虽然认为基本没有这个必要性,但还是让用户可以有选择的使用本接口
 * 
 * 
 * 
 * @author jzoom
 *
 */
public interface NameAdapterFactory {
	
	NameAdapter getNameAdapter(String table);
	
	NameAdapter getNameAdapter(String[] tables);
}
