package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.driver.SqlDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 这个类不做任何字段的过滤和处理
 *
 * @author jzoom
 */
public class SimpleSqlBuilder implements SqlBuilder {
    protected static final char SPACE = ' ';

    protected StringBuilder sql;


    protected StringBuilder where;
    protected StringBuilder orderBy;
    protected StringBuilder groupBy;
    protected StringBuilder join;
    protected StringBuilder select;

    protected List<Object> values;
    protected List<StatementAdapter> adapters;

    protected String table;
    protected StringBuilder having;
    protected SqlDriver driver;


    protected Record record;

    public SimpleSqlBuilder(SqlDriver driver) {
        this.driver = driver;
        sql = new StringBuilder();
        where = new StringBuilder();
        orderBy = new StringBuilder();
        join = new StringBuilder();
        groupBy = new StringBuilder();
        select = new StringBuilder();
        having = new StringBuilder();
        record = new Record();
        values = new ArrayList<Object>();
        adapters = new ArrayList<StatementAdapter>();
    }


    public void buildLimit(int position, int size) {
        buildSelect();
        driver.buildLimit(sql, values, position, size);
    }


    public void buildSelect() {
        sql.append("SELECT");
        if (select.length() == 0) {
            sql.append(" *");
        } else {
            sql.append(" ").append(select);
        }
        sql.append(" FROM ")
                .append(table)
                .append(join)
                .append(where)
                .append(groupBy)
                .append(having)
                .append(orderBy);
    }

    public void buildUpdate() {
        BuilderKit.buildUpdate(sql, values, driver, table, where, record);
    }


    public void buildInsert() {
        BuilderKit.buildInsert(sql, values, driver, table, record);
    }

    public void buildDelete() {
        BuilderKit.buildDelete(sql, table, where);
    }

    @Override
    public String printSql() {
        return String.format(sql.toString().replace("?", "'%s'"),
                values.toArray(new Object[values.size()]));
    }

    @Override
    public SqlBuilder tables(String... tables) {
        assert (tables.length > 0);
        table = tables[0];
        return this;
    }

    public void insertOrUpdate(String[] keys) {
        values.clear();
        sql.setLength(0);
        driver.insertOrUpdate(sql, values, table, record, keys);
    }


    public void clear(boolean all) {
        if (all) {
            where.setLength(0);
            join.setLength(0);
            groupBy.setLength(0);
            values.clear();
            having.setLength(0);
            adapters.clear();
        }

        sql.setLength(0);
        orderBy.setLength(0);
        record.clear();
        select.setLength(0);
    }


    private void andWhere() {
        whereRelation(AND);
    }

    private void orWhere() {
        whereRelation(OR);
    }

    private void whereRelation(String relation) {
        if (condition) {
            condition = false;
        } else {
            if (where.length() == 0) {
                return;
            } else {
                where.append(relation);
            }
        }

    }


    @Override
    public SqlBuilder like(String name, Like like, Object value) {
        return relationLike(name, like, value, AND, false);
    }

    private SqlBuilder relationLike(String name, Like like, Object value, String relation, boolean not) {
        assert (name != null);
        checkValue(value);
        whereRelation(relation);

        where.append(name);
        if (not) {
            where.append(" NOT");
        }
        where.append(" LIKE ?");
        addValue(name, like.toValue(value));
        return this;
    }

    @Override
    public SqlBuilder orLike(String name, Like like, Object value) {
        return relationLike(name, like, value, OR, false);
    }

    private void checkValue(Object value) {
        if (value == null) {
            throw new RuntimeException("值为null?请使用whereNull或者whereNotNull版本");
        }
    }

    @Override
    public SqlBuilder where(String name, Symbol symbol, Object value) {
        assert (name != null && symbol != null);

        return whereImpl(where, name, symbol, value, AND);

    }

    protected SqlBuilder whereImpl(StringBuilder where, String name, Symbol symbol, Object value, String relation) {
        checkValue(value);
        whereRelation(relation);
        where.append(name).append(symbol.value()).append("?");
        addValue(name, value);
        return this;
    }

    @Override
    public SqlBuilder where(String name, Object value) {
        return whereImpl(where, name, Symbol.EQ, value, AND);
    }


    @Override
    public SqlBuilder orWhere(String name, Object value) {

        return orWhere(name, Symbol.EQ, value);
    }

    @Override
    public SqlBuilder orWhere(String name, Symbol symbol, Object value) {
        return whereImpl(where, name, symbol, value, OR);
    }

    @Override
    public SqlBuilder whereNull(String name) {
        return whereNull(name, AND, false);
    }

    private SqlBuilder whereNull(String name, String relation, boolean not) {
        whereRelation(relation);
        StringBuilder where = this.where;
        where.append(name);
        where.append(" IS ");
        if (not) where.append("NOT ");
        where.append("NULL");

        return this;
    }

    @Override
    public SqlBuilder whereNotNull(String name) {
        return whereNull(name, AND, true);
    }

    @Override
    public SqlBuilder whereNotIn(String name, Object... values) {
        return whereIn(name, AND, true, values);
    }


    private static final String AND = " AND ";
    private static final String OR = " OR ";

    protected SqlBuilder whereIn(String name, String relation, boolean not, Object... values) {
        StringBuilder where = this.where;
        whereRelation(relation);
        where.append(name);
        if (not) {
            where.append(" NOT");
        }
        where.append(" IN (");
        boolean first = true;
        for (Object object : values) {
            if (first) {
                first = false;
            } else {
                where.append(",");
            }
            where.append("?");
            this.addValue(name, object);
        }
        where.append(')');

        return this;
    }

    @Override
    public SqlBuilder whereIn(String name, Object... values) {
        return whereIn(name, AND, false, values);
    }

    @Override
    public SqlBuilder join(String otherTable, String on) {
        return join(otherTable, on, "INNER");
    }

    @Override
    public SqlBuilder join(String table, String on, String type) {
        if (type == null) {
            type = "INNER";
        }
        join.append(SPACE)
                .append(type).append(" JOIN ").append(table).append(" ON ").append(on);
        return this;
    }


    private void addValue(String name, Object value) {

        this.values.add(value);
    }


    @Override
    public SqlBuilder orderBy(String field, Sort sort) {
        assert (sort != null && field != null);
        if (orderBy.length() != 0) {
            orderBy.append(',');
        } else {
            orderBy.append(" ORDER BY ");
        }
        orderBy.append(field).append(SPACE).append(sort.value());
        return this;
    }

    @Override
    public SqlBuilder groupBy(String group) {
        groupBy.append(" GROUP BY ").append(group);
        return this;
    }

    @Override
    public SqlBuilder table(String table) {
        this.table = table;
        return this;
    }


    public int position2page(int position, int size) {
        return driver.position2page(position, size);
    }


    @Override
    public SqlBuilder selectCount(String alias) {
        selectRaw("COUNT(*) AS COUNT");
        return this;
    }


    public SqlBuilder selectRaw(String fields) {
        select.append(fields);
        return this;
    }


    @Override
    public SqlBuilder select(String fields) {
        parseSelect(select, fields);
        return this;
    }

    public SqlBuilder select(Iterable<String> fields) {
        parseSelect(select, fields);
        return this;
    }

    private static final Log log = LogFactory.getLog(SimpleSqlBuilder.class);

    /**
     * select 中的形式有 函数(字段,字段) as 字段 , 字段 as 字段,
     *
     * @param sql
     * @param select
     */
    protected void parseSelect(StringBuilder sql, String select) {
        if (select.isEmpty()) return;
        String[] parts = select.split(",");
        parseSelect(sql, parts);
    }

    protected void parseSelect(StringBuilder sql, String[] parts) {
        boolean first = true;
        for (String part : parts) {
            if (first) {
                first = false;
            } else {
                sql.append(",");
            }
            parseSelectColumn(sql, part);
        }
    }


    protected void parseSelect(StringBuilder sql, Iterable<String> parts) {
        boolean first = true;
        for (String part : parts) {
            if (first) {
                first = false;
            } else {
                sql.append(",");
            }
            parseSelectColumn(sql, part);
        }
    }

    /**
     * column 里面的形式为 SELECT 中的字段,所以可能需要在子类过滤一下非法查询
     *
     * @param sql
     * @param column
     */
    protected void parseSelectColumn(StringBuilder sql, String column) {
        if(column.contains("'")){
            throw new DaoException("字段中不能包含',具体的值必须写在参数中");
        }
        sql.append(column);
    }

    /**
     * condition 里面的形式为 WHERE 中的查询条件,所以可能需要在子类过滤一下非法查询
     *
     * @param where
     * @param condition
     */
    protected void parseCondition(StringBuilder where, String condition) {
        where.append(condition);
    }

    @Override
    public SqlBuilder set(String name, Object value) {
        record.put(name, value);
        return this;
    }

    @Override
    public SqlBuilder setAll(Map<String, Object> data) {
        record.putAll(data);
        return this;
    }

    @Override
    public SqlBuilder clear() {
        clear(true);
        return this;
    }

    @Override
    public List<Object> values() {
        return values;
    }

    @Override
    public String sql() {
        return sql.toString();
    }

    boolean condition;


    /**
     * 这个版本暂时原封不动，下个版本解析
     *
     * @param name
     * @param values
     * @return
     */
    @Override
    public SqlBuilder whereCondition(String name, Object... values) {
        // parse the name ,it should be  group of
        // a >/</=/>=/<=/<> ?
        // a in ( ?,? )
        // not a is null
        // a is null
        // (group)
        // group and group
        // group or group
        // a like '%xx%'
        // a not like '%xx%'
        // a in/exists (select xx from b) is not allowed!
        parseCondition(where, name);
        Collections.addAll(this.values, values);
        return this;
    }


    void conditionWhere(Condition condition) {
        where.append("(");
        this.condition = true;
        condition.where(this);
        if (this.condition) {
            throw new DaoException("Condition下至少需要一个条件");
        }
        where.append(")");
    }

    @Override
    public SqlBuilder where(Condition condition) {
        andWhere();
        conditionWhere(condition);
        return this;
    }

    @Override
    public SqlBuilder orWhere(Condition condition) {
        orWhere();
        conditionWhere(condition);
        return this;

    }


    @Override
    public SqlBuilder having(String name, Symbol symbol, Object value) {

        if (sql.length() == 0) {
            having.append(" HAVING ");
        } else {
            having.append(AND);
        }

        whereImpl(having, name, symbol, value, AND);

        return this;

    }

    @Override
    public SqlBuilder notLike(String name, Like like, Object value) {

        return relationLike(name, like, value, AND, false);
    }

    @Override
    public SqlBuilder union(SqlBuilder builder) {
        throw new NotImplementedException("union");
    }

    @Override
    public SqlBuilder unionAll(SqlBuilder builder) {
        throw new NotImplementedException("unionAll");
    }


    /**
     * 使用本方法调用 SELECT max(ID) AS ID_ 之类的select字段
     * {@link SimpleSqlBuilder#selectMax}
     *
     * @param field
     * @param function
     * @return
     */
    protected SqlBuilder selectFunc(String field,String alias, String function) {
        if(select.length()>0){
            select.append(",");
        }
        select.append(function).append("(");
        parseSelect(select, field);
        select.append(")");
        select.append(" AS ").append(alias);
        return this;
    }

    @Override
    public SqlBuilder selectMax(String field,String alias) {
        return selectFunc(field, alias,"MAX");
    }

    @Override
    public SqlBuilder selectSum(String field,String alias) {
        return selectFunc(field, alias,"SUM");
    }

    @Override
    public SqlBuilder selectMin(String field,String alias) {
        return selectFunc(field, alias,"MIN");
    }

    @Override
    public SqlBuilder selectAvg(String field,String alias) {
        return selectFunc(field, alias,"AVG");
    }

}
