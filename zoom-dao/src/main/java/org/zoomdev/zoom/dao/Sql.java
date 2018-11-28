package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.common.expression.Symbol;

/**
 * 与SQL语句相关的方法
 *
 * @param <T>
 */
public interface Sql<T extends Sql> {

    /**
     * dao.ar("table").where("id",1).find()=>select * from table where id=?
     *
     * @param key
     * @param value
     * @return
     */
    T where(String key, Object value);

    /**
     * dao.ar("table").where("id",Symbol.GT,1).find()=>select * from table where id>?
     *
     * @param key
     * @param symbol
     * @param value
     * @return
     */
    T where(String key, Symbol symbol, Object value);

    /**
     * .where("id",2).where(new Condition(){
     * public void where(SqlBuilder where){
     * where.where("id",1).orWhere("name","张三")
     * }
     * })=>  id=? and (id = ? or name=?)
     *
     * @param condition
     * @return
     */
    T where(SqlBuilder.Condition condition);

    /**
     * where("id",1,2,3)=> where id in (?,?,?)
     *
     * @param key
     * @param values
     * @param <E>
     * @return
     */
    <E> T whereIn(String key, E... values);

    /**
     * whereNotNull("name")=> where name is not null
     *
     * @param key
     * @return
     */
    T whereNotNull(String key);

    /**
     * whereNull("name")=> where name is null
     *
     * @param key
     * @return
     */
    T whereNull(String key);

    /**
     * like("name",Like.MATCH_LEFT,"张")=> name like ? => '张%'
     *
     * @param key
     * @param like
     * @param value
     * @return
     */
    T like(String key, SqlBuilder.Like like, Object value);

    /**
     * .where("id",2).orWhere(new Condition(){
     * public void where(SqlBuilder where){
     * where.where("id",1).orWhere("name","张三")
     * }
     * })=>  id=? or (id = ? or name=?)
     *
     * @param condition
     * @return
     */
    T orWhere(SqlBuilder.Condition condition);

    /**
     * where("id",1).orWhere("id",2)=> id=? or id=?
     *
     * @param key
     * @param value
     * @return
     */
    T orWhere(String key, Object value);


    T orLike(String name, SqlBuilder.Like like, Object value);

    /**
     * orderBy("id",Sort.DESC)=> order by id desc
     * orderBy("id",Sort.DESC).orderBy("name",Sort.ASC)=> order by id desc,name asc
     *
     * @param key
     * @param sort
     * @return
     */
    T orderBy(String key, SqlBuilder.Sort sort);

    /**
     * groupBy("id")=> group by id
     * groupBy("id,name")=> group by id,name
     *
     * @param key
     * @return
     */
    T groupBy(String key);

    /**
     * having("AVG(score)",Symbol.GT,60)=> having AVG(score) > 60
     *
     * @param key
     * @param symbol
     * @param value
     * @return
     */
    T having(String key, Symbol symbol, Object value);

    /**
     * dao.ar("table").where("id",1).union(
     * dao.builder().table("table").where("name","张三")
     * ).find()=>  (select * from table where id=?) union (select * from table where name=?)
     *
     * @param sqlBuilder
     * @return
     */
    T union(SqlBuilder sqlBuilder);

    /**
     * dao.ar("table").where("id",1).union(
     * dao.builder().table("table").where("name","张三")
     * ).find()=>  (select * from table where id=?) union all (select * from table where name=?)
     *
     * @param sqlBuilder
     * @return
     */
    T unionAll(SqlBuilder sqlBuilder);


    /**
     * join("order","order.id=product.id")=> inner join order on order.id=product.id
     *
     * @param table
     * @param on
     * @return
     */
    T join(String table, String on);

    /**
     * join("order","order.id=product.id","left")=> left join order on order.id=product.id
     *
     * @param table
     * @param on
     * @param type
     * @return
     */
    T join(String table, String on, String type);


    /**
     * select("id,name")=> select id,name
     *
     * @param select
     * @return
     */
    T select(String select);

    /**
     * select(Array.asList("id","name"))=> select id,name
     *
     * @param select
     * @return
     */
    T select(Iterable<String> select);


}
