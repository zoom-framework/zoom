package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 接口
 * 
 * Ar extends SetSource
 * 
 * SetSource extends Result
 * 
 * 
 * where->Where
 * 
 * Where extends Result
 * 
 * Having,GroupBy,OrderBy extends Result
 * 
 * 
 * Result 
 * 
 * 
 * 
 * 
 * ar.table().join().where
 * @author jzoom
 *
 */
public class SimpleSqlBuilder implements SqlBuilder {
	protected static final char SPACE = ' ';

	protected StringBuilder sql;
	protected StringBuilder where;
	protected StringBuilder orderBy;
	protected StringBuilder groupBy;
	protected StringBuilder join;
	protected List<String> select;

	protected List<Object> values;
	protected List<StatementAdapter> adapters;

	protected String table;
	protected StringBuilder having;
	protected SqlDriver driver;


	protected Record record;

	public SimpleSqlBuilder(Dao dao) {
		this.driver = dao.getDriver();

		sql = new StringBuilder();
		where = new StringBuilder();
		orderBy = new StringBuilder();
		join = new StringBuilder();
		groupBy = new StringBuilder();
		select = new ArrayList<String>();
		having = new StringBuilder();
		record = new Record();
		values = new ArrayList<Object>();
        adapters = new ArrayList<StatementAdapter>();

	}

	public String printSql(){
        return String.format(sql.toString().replace("?", "'%s'"), values.toArray(new Object[values.size()]));
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
		driver.insertOrUpdate(sql,values,table, record,keys);
	}

	public void clear( boolean all ) {
		if(all) {
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
		select.clear();
	}


	private void andWhere() {
		if (where.length() == 0) {
			where.append(" WHERE ");
		} else {
			where.append(" AND ");
		}
	}

	@Override
	public SqlBuilder like(String name, Like like, Object value) {
		assert (name != null && name != null);
		checkValue(value);
		andWhere();
		where.append(name).append(" LIKE ?");
		addValue(name, like.toValue(value));
		return this;
	}

	private void checkValue(Object value) {
		if (value == null) {
			throw new RuntimeException("值为null?请使用whereNull或者whereNotNull版本");
		}
	}

	@Override
	public SqlBuilder where(String name, Symbol symbol, Object value) {
		assert (name != null && symbol != null);

		return whereImpl(name, symbol, value, " AND ");

	}

	protected SqlBuilder whereImpl(String name, Symbol symbol, Object value, String relation) {
		checkValue(value);
		if (where.length() == 0) {
			where.append(" WHERE ");
		} else {
			where.append(relation);
		}
		where.append(name).append(symbol.value()).append("?");
		addValue(name, value);
		return this;
	}

	@Override
	public SqlBuilder where(String name, Object value) {
		return whereImpl(name, Symbol.EQ, value, " AND ");
	}



	@Override
	public SqlBuilder orWhere(String name, Object value) {

		return orWhere(name, Symbol.EQ, value);
	}

	@Override
	public SqlBuilder orWhere(String name, Symbol symbol, Object value) {
		return whereImpl(name, symbol, value, " OR ");
	}

	@Override
	public SqlBuilder whereNull(String name) {
		andWhere();
		where.append(name).append(" IS NULL");
		return this;
	}

	@Override
	public SqlBuilder whereNotNull(String name) {
		andWhere();
		where.append(" NOT (").append(name).append(" IS NULL)");
		return this;
	}

	@Override
	public SqlBuilder whereIn(String name, Object... values) {
		andWhere();
		where.append(name).append(" IN (");
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
	public SqlBuilder join(String otherTable, String on) {

		return join(otherTable, on, "INNER");
	}

	@Override
	public SqlBuilder join(String table, String on, String type) {
        if (type == null) {
            type = "INNER";
        }
		join.append(SPACE).append(type).append(" JOIN ").append(table).append(" ON ").append(on);
		return this;
	}


	private void addValue(String name, Object value) {

		this.values.add(value);
	}


	@Override
	public SqlBuilder orderBy(String field, Sort sort) {
		assert (sort != null && field != null);
		if (orderBy.length() == 0) {
			orderBy.append(" ORDER BY ");
		} else {
			orderBy.append(',');
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


	public void buildLimit(int position, int size) {
		buildSelect();
		driver.buildPage(sql, position, size);
	}
	
	public int getPageFromPosition(int position,int size) {
		return driver.position2page(position,size);
	}
	
	


	@Override
	public SqlBuilder count() {
		selectRaw("COUNT(*) AS COUNT");
		return this;
	}

	
	public SqlBuilder selectRaw(String fields) {
        if(sql.length() == 0) {
            sql.append("SELECT ");
        }else{
            sql.append(",");
        }
        sql.append(fields);
        return this;
	}


	@Override
	public SqlBuilder select(String fields) {
		if(sql.length() == 0) {
			sql.append("SELECT ");
		}else {
			sql.append(",");
		}
		parseSelect(sql, fields);
		return this;
	}

	public SqlBuilder select(Iterable<String> fields) {
		if(sql.length() == 0) {
			sql.append("SELECT ");
		}else {
			sql.append(",");
		}

		parseSelect(sql,fields);

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
		if ("*".equals(select)) {
			sql.append("*");
			return;
		}
		String[] parts = select.split(",");
		parseSelect(sql,parts);
	}
	protected void parseSelect(StringBuilder sql,String[] parts){
		Matcher matcher;
		boolean first = true;
		for (String part : parts) {
			if (first) {
				first = false;
			} else {
				sql.append(",");
			}
			parsePart(sql,part);
		}
	}


	protected void parsePart(StringBuilder sql,String part){
		Matcher matcher;
		if ((matcher = BuilderKit.AS_PATTERN.matcher(part)).matches()) {
			driver.protectColumn(sql,matcher.group(1));
            sql.append(" AS ");
            driver.protectColumn(sql, matcher.group(2));
		} else {
			if (part.contains("(")) {
				//纯粹的函数?
				throw new DaoException("如果需要在查询中使用函数，请使用selectMax/selectMin等select系列方法，或者使用selectRaw,并注意SQL注入问题");
				//sql.append(part);
			} else {
                driver.protectColumn(sql, part);
			}
		}
	}

	protected void parseSelect(StringBuilder sql,Iterable<String> parts){

		boolean first = true;
		for (String part : parts) {
			if (first) {
				first = false;
			} else {
				sql.append(",");
			}
			parsePart(sql,part);
		}
	}


	public void buildSelect() {
		if (sql.length() == 0) {
			sql.append("SELECT * ");
		}
		sql.append(" FROM ");
		driver.protectTable(sql, table);
		sql.append(join)
			.append(where)
			.append(groupBy)
			.append(having)
			.append(orderBy);
	}

	public List<Object> getValues() {
		return values;
	}


    public void buildUpdate(Map<String, Object> record) {
		if (record != null)
			setAll(record);
		BuilderKit.buildUpdate(sql, values, driver, table, where, this.record);
	}

	public void buildUpdate() {
		BuilderKit.buildUpdate(sql, values, driver, table, where, this.record);
	}

	public void buildInsert() {
		BuilderKit.buildInsert(sql, values, driver, table, this.record);
	}

	public void buildDelete() {
		BuilderKit.buildDelete(sql, table, where);
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
	public SqlBuilder where(Condition condition) {
		condition.where(this);
		return this;
	}


	@Override
	public SqlBuilder whereCondition(String name, Object... values) {
		if(where.length() == 0) {
			where.append("WHERE ");
		}
		// parse the name ,it should be  group of
		// a >/</=/>=/<=/<> ? 
		// a in ( ?,? )
		// not a is null
		// a is null
		// (group)
		// group and group
		// group or group
		// a in (select xx from b) is not allowed!
		
		where.append(name);
		Collections.addAll(this.values, values);
		return this;
	}

	@Override
	public SqlBuilder orWhere(Condition condition) {
		throw new NotImplementedException("orWhere");
	}

	@Override
	public SqlBuilder whereNotIn(String name, Object... values) {
		throw new NotImplementedException("whereNotIn");
	}

	@Override
	public SqlBuilder having(String name, Symbol symbol, Object value) {
		throw new NotImplementedException("having");
	}
	@Override
	public SqlBuilder notLike(String name, Like like, Object value) {
		throw new NotImplementedException("notLike");
	}

	@Override
	public SqlBuilder union(SqlBuilder builder) {
		throw new NotImplementedException("union");
	}

	@Override
	public SqlBuilder unionAll(SqlBuilder builder) {
		throw new NotImplementedException("unionAll");
	}

	protected SqlBuilder selectFunc(String field,String function){
		if(sql.length() == 0) {
			sql.append("SELECT ");
		}else {
			sql.append(",");
		}
		sql.append(function).append("(");
		parseSelect(sql,field);
		sql.append(")");
		sql.append(" AS ").append(field);

		return this;
	}

	@Override
	public SqlBuilder selectMax(String field) {
		return selectFunc(field,"MAX");
	}

	@Override
	public SqlBuilder selectSum(String field) {
		return selectFunc(field,"SUM");
	}

	@Override
	public SqlBuilder selectMin(String field) {
		return selectFunc(field,"MIN");
	}
	@Override
	public SqlBuilder selectAvg(String field) {
		return selectFunc(field,"AVERAGE");
	}

}
