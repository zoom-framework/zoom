package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.http.expression.Symbol;

import java.util.List;
import java.util.Map;

public interface SqlBuilder extends Sql<SqlBuilder> {

    String INNER = "INNER";
    String LEFT = "LEFT";
    String RIGHT = "RIGHT";


    enum JoinType{
        INNER(SqlBuilder.INNER),
        LEFT(SqlBuilder.LEFT),
        RIGHT(SqlBuilder.RIGHT);

        private String value;

        JoinType(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public static JoinType parse(String type) {
            if (type == null)
                return INNER;

            type = type.toUpperCase();

            for (JoinType s : values()) {
                if (s.value.equals(type)) {
                    return s;
                }
            }

            return INNER;
        }
    }

    enum Sort {
        ASC("ASC"),
        DESC("DESC");

        private String value;

        Sort(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public static Sort parse(String sort) {
            if (sort == null)
                return DESC;

            sort = sort.toUpperCase();

            for (Sort s : values()) {
                if (s.value.equals(sort)) {
                    return s;
                }
            }

            return DESC;
        }

    }

    public enum Join {
        INNER("INNER"),
        LEFT("LEFT"),
        RIGHT("RIGHT");

        Join(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        private String value;

    }


    /**
     * @author jzoom
     */
    public enum Like {

        MATCH_BOTH("%%%s%%"),                //like '%{}%'
        MATCH_END("%%%s"),                //like '%{}'
        MATCH_START("%s%%");                //like '{}%'

        Like(String format) {
            this.format = format;
        }

        private String format;

        public String toValue(Object value) {
            return String.format(format, value);
        }
    }


    public interface Condition {
        void where(Sql where);
    }

    /**
     * 等于
     *
     * @param name
     * @param value
     * @return
     */
    SqlBuilder where(String name, Object value);

    /**
     * @param name
     * @param like
     * @param value
     * @return
     */
    SqlBuilder like(String name, Like like, Object value);


    SqlBuilder orLike(String name, Like like, Object value);

    /**
     * @param name
     * @param symbol > < >= <= <> =
     * @param value
     * @return
     */
    SqlBuilder where(String name, Symbol symbol, Object value);


    /**
     * where {name} is null
     *
     * @param name
     * @return
     */
    SqlBuilder whereNull(String name);

    /**
     * where {name} in (  ?,?,?  ), 1,2,3
     *
     * @param name
     * @param values
     * @return
     */
    SqlBuilder whereIn(String name, Object... values);

    /**
     * 相当于 where    (    condition     )
     *
     * @param where
     * @return
     */
    SqlBuilder where(Condition where);

    /**
     * having('selectSum(user)',Symbo.gt,50)
     *
     * @return
     */
    SqlBuilder having(String name, Symbol symbol, Object value);


    SqlBuilder orWhere(String name, Object value);

    SqlBuilder orWhere(String name, Symbol symbol, Object value);

    /**
     * whereCondition ("a=? and b=? and c=?", 1 , 2, 3)
     * 注意这里不要写成  a=1 and b=2 and c=3,
     *
     * @param value
     * @return
     */
    SqlBuilder whereCondition(String value, Object... values);


    /**
     * or where ...
     *
     * @param condition
     * @return
     */
    SqlBuilder orWhere(Condition condition);


    SqlBuilder whereNotNull(String name);


    SqlBuilder whereNotIn(String name, Object... values);

    /**
     * @param name
     * @param like
     * @param value
     * @return
     */
    SqlBuilder notLike(String name, Like like, Object value);


    /**
     * .innerJoin("table_a","table_a.id=table_b.id",)
     *
     * @param otherTable
     * @param on
     * @return
     */
    SqlBuilder join(String otherTable, String on);

    SqlBuilder join(String table, String on, String type);

    SqlBuilder join(String table, String on, JoinType type);



    SqlBuilder union(SqlBuilder builder);


    SqlBuilder unionAll(SqlBuilder builder);


    /**
     * 按照排序
     *
     * @param field
     * @param sort
     * @return
     */
    SqlBuilder orderBy(String field, Sort sort);


    SqlBuilder groupBy(String field);


    /**
     * 指定表
     *
     * @param table
     * @return
     */
    SqlBuilder table(String table);


    /**
     * select("selectSum(count) as count,selectMin(id),a ,b ","c","d")
     *
     * @param fields
     * @return
     */
    SqlBuilder select(String fields);

    SqlBuilder selectSum(String field, String alias);

    SqlBuilder selectMax(String field, String alias);

    SqlBuilder selectMin(String field, String alias);

    SqlBuilder selectCount(String alias);

    SqlBuilder selectAvg(String field, String alias);


    SqlBuilder set(String name, Object value);

    SqlBuilder setAll(Map<String, Object> data);

    SqlBuilder clear();


    List<Object> values();

    void buildSelect();

    void buildDelete();

    void buildUpdate();

    void buildInsert();

    void buildLimit(int position, int limit);

    String sql();

    String printSql();

}
