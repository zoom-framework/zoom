package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.common.expression.Symbol;

/**
 * 与SQL语句相关的方法
 *
 * @param <T>
 */
public interface Sql<T extends Sql> {


    String INNER = "INNER";
    String LEFT = "LEFT";
    String RIGHT = "RIGHT";




    T where(String key,Object value);

    T orWhere(String key, Object value);

    <E> T whereIn(String key, E... values);

    T like(String name, SqlBuilder.Like like, Object value);

    T whereCondition(String key,Object...values);

    T where(String key, Symbol symbol, Object value);


    /// order by
    T orderBy(String field, SqlBuilder.Sort sort);

    /// group by
    T groupBy(String field);

    /// having
    T having(String field,Symbol symbol,Object value);


    /// union
    T union(SqlBuilder sqlBuilder);

    T unionAll(SqlBuilder sqlBuilder);


    // 多表查询
    T join(String table,String on);

    T join(String table, String on, String type);



    /// 选择查询字段
    T select(String select);

    T select(Iterable<String> select);


}
