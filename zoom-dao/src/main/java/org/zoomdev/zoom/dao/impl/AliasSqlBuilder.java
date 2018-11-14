package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.adapters.NameAdapter;
import org.zoomdev.zoom.dao.alias.NameAdapterFactory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AliasSqlBuilder extends SimpleSqlBuilder {
    private NameAdapterFactory aliasPolicyManager;
    NameAdapter nameAdapter;

    private Dao dao;


    public AliasSqlBuilder(Dao dao) {
        super(dao.getDriver());
        this.dao = dao;
        this.aliasPolicyManager = dao.getNameAdapterFactory();
    }

    @Override
    public void clear(boolean all) {
        if (all) {
            nameAdapter = null;
        }

        super.clear(all);
    }

    //	@Override
//	public SqlBuilder join(String table, String on, String type) {
//		tableNames.add(table);
//		nameAdapter = aliasPolicyManager.getNameAdapter( tableNames.toArray(
//				new String[ tableNames.size() ]) );
//		return super.join(table, on, type);
//	}
//
    @Override
    public SqlBuilder table(String table) {
        nameAdapter = aliasPolicyManager.getNameAdapter(table);
        return super.table(table);
    }

    @Override
    public SqlBuilder tables(String... tables) {
        nameAdapter = aliasPolicyManager.getNameAdapter(tables);
        return super.tables(tables);
    }


    @Override
    public SqlBuilder orderBy(String field, Sort sort) {
        field = nameAdapter.getColumnName(field);
        return super.orderBy(field, sort);
    }

    @Override
    protected SqlBuilder whereImpl(StringBuilder where,String name, Symbol symbol, Object value, String relation) {
        name = nameAdapter.getColumnName(name);
        return super.whereImpl(where,name, symbol, value, relation);
    }

    @Override
    public SqlBuilder like(String name, Like like, Object value) {
        name = nameAdapter.getColumnName(name);
        return super.like(name, like, value);
    }

    @Override
    public SqlBuilder setAll(Map<String, Object> data) {
        assert (data != null);
        for (Entry<String, Object> entry : data.entrySet()) {
            String name = entry.getKey();
            super.set(nameAdapter.getColumnName(name), entry.getValue());
        }

        return this;
    }

    @Override
    public SqlBuilder set(String name, Object value) {
        name = nameAdapter.getColumnName(name);
        return super.set(name, value);
    }



    @Override
    protected void parseSelectColumn(StringBuilder sql, String part) {
        Matcher matcher;
        if ((matcher = BuilderKit.AS_PATTERN.matcher(part)).matches()) {
            driver.protectColumn(sql, matcher.group(1));
            sql.append(" AS ");
            driver.protectColumn(sql, nameAdapter.getColumnName(matcher.group(2)));
        } else {
            if (part.contains("(")) {
                //纯粹的函数?
                throw new DaoException("如果需要在查询中使用函数，请使用selectMax/selectMin等select系列方法，或者使用selectRaw,并注意SQL注入问题");
                //sql.append(part);
            } else {
                //使用标准的改名策略,如果是单表，那么按照单表来解析
                String column = nameAdapter.getSelectColumn(part);
                if (column == null) {
                    throw new DaoException("找不到字段" + part);
                }
                sql.append(column);
                //driver.protectColumn(sql, column);
            }
        }
    }


    private Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)");

    @Override

    public SqlBuilder join(String table, String on, String type) {

        //解析on,实际上就是一个字符串替换
        Matcher matcher = pattern.matcher(on);
        StringBuilder sb = new StringBuilder();
        int start = 0;
        while (matcher.find()) {

            String name = matcher.group(1);
            String column = nameAdapter.getColumnName(name);

            sb.append(on.substring(start, matcher.start(1)))
                    .append(column);
            start = matcher.end(1);
        }
        sb.append(on.substring(start, on.length()));

        return super.join(table, sb.toString(), type);
    }



}
