package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.dao.Sql;
import org.zoomdev.zoom.dao.SqlBuilder;

public class AbstractRecord<T extends Sql> implements Sql<T> {




    @Override
    public T orWhere(SqlBuilder.Condition condition) {
        return null;
    }

    @Override
    public T where(String key, Object value) {
        return null;
    }

    @Override
    public T orWhere(String key, Object value) {
        return null;
    }

    @Override
    public T whereNotNull(String name) {
        return null;
    }

    @Override
    public <E> T whereIn(String key, E... values) {
        return null;
    }

    @Override
    public T like(String name, SqlBuilder.Like like, Object value) {
        return null;
    }

    @Override
    public T where(String key, Symbol symbol, Object value) {
        return null;
    }

    @Override
    public T orderBy(String field, SqlBuilder.Sort sort) {
        return null;
    }

    @Override
    public T groupBy(String field) {
        return null;
    }

    @Override
    public T having(String field, Symbol symbol, Object value) {
        return null;
    }

    @Override
    public T union(SqlBuilder sqlBuilder) {
        return null;
    }

    @Override
    public T unionAll(SqlBuilder sqlBuilder) {
        return null;
    }

    @Override
    public T join(String table, String on) {
        return null;
    }

    @Override
    public T join(String table, String on, String type) {
        return null;
    }

    @Override
    public T select(String select) {
        return null;
    }

    @Override
    public T select(Iterable<String> select) {
        return null;
    }

    @Override
    public T whereNull(String field) {
        return null;
    }
}
