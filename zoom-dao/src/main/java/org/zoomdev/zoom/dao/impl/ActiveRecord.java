package org.zoomdev.zoom.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.alias.NameAdapter;
import org.zoomdev.zoom.dao.utils.DaoUtils;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ActiveRecord extends AbstractRecord implements ConnectionHolder, Ar {


    private static final Log log = LogFactory.getLog(ActiveRecord.class);

    private NameAdapter nameAdapter;

    private boolean output;

    private NameAdapter defaultNameAdapter;

    public ActiveRecord(Dao dao, NameAdapter nameAdapter, boolean output) {
        super(dao.getDataSource(), new SimpleSqlBuilder(dao.getDriver()));
        this.nameAdapter = nameAdapter;
        this.defaultNameAdapter = nameAdapter;
        this.output = output;
    }

    public List<Record> query() {

        return execute(new ConnectionExecutor() {
            @Override
            public List<Record> execute(Connection connection) throws SQLException {
                return BuilderKit.executeQuery(connection, builder, nameAdapter, output);
            }
        });
    }

    public Record get(final String sql, final List<Object> values,
                      final NameAdapter nameAdapter) {
        return execute(new ConnectionExecutor() {
            @Override
            public Record execute(Connection connection) throws SQLException {
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    ps = BuilderKit.prepareStatement(connection, sql, values, output);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        return BuilderKit.buildOne(rs, nameAdapter);
                    }
                    return null;
                } finally {
                    DaoUtils.close(rs);
                    DaoUtils.close(ps);
                }
            }
        });
    }


    public ResultSet execute(final String sql,
                             final List<Object> values,
                             final boolean all) {
        return execute(new ConnectionExecutor() {

            @SuppressWarnings("unchecked")
            @Override
            public ResultSet execute(Connection connection) throws SQLException {
                ResultSet rs = null;
                PreparedStatement ps = null;
                try {
                    connection = getConnection();
                    ps = BuilderKit.prepareStatement(connection, sql, values, output);
                    rs = ps.executeQuery();
                    return rs;
                } finally {
                    DaoUtils.close(ps);
                }
            }
        });
    }


    @Override
    public List<Record> find() {
        builder.buildSelect();
        return query();
    }

    @Override
    public Record get() {
        builder.buildSelect();
        return get(builder.sql.toString(),
                builder.values,
                nameAdapter);
    }


    @Override
    public List<Record> limit(int position, int size) {
        builder.buildLimit(position, size);
        return query();
    }

    @Override
    public Page<Record> page(int page, int size) {
        if (page <= 0)
            page = 1;
        return position((page - 1) * size, size);
    }

    @Override
    public Page<Record> position(final int position, final int size) {

        return execute(new ConnectionExecutor() {
            @Override
            public Page<Record> execute(Connection connection) throws SQLException {
                builder.buildLimit(position, size);
                List<Record> list = BuilderKit.executeQuery(connection, builder, nameAdapter, output);
                builder.clear(false);
                remove2(builder.values);
                int total = EntitySqlUtils.getValue(connection, builder, DaoUtils.SELECT_COUNT, int.class, output);
                int page = DaoUtils.position2page(position, size);
                return new Page<Record>(list, page, size, total);
            }
        });

    }


    @Override
    public Ar table(String table) {
        builder.table(table);
        return this;
    }

    /**
     * 直接指定
     *
     * @param nameAdapter
     * @return
     */
    @Override
    public Ar nameAdapter(NameAdapter nameAdapter) {
        this.nameAdapter = nameAdapter;
        return this;
    }


    @Override
    public int insertOrUpdate(String... keys) {
        builder.insertOrUpdate(keys);
        return _executeUpdate();
    }


    @SuppressWarnings("unchecked")
    @Override
    public int insert(Map<String, Object> data) {
        assert (data != null);
        builder.setAll(data);
        return insert();
    }

    @Override
    public int insert() {
        builder.buildInsert();
        return _executeUpdate();
    }

    @Override
    public int insert(Record data, String... generateKeys) {
        builder.record.putAll(data);
        builder.buildInsert();
        return _executeInsert(data, generateKeys);
    }

    @Override
    public int update() {
        builder.buildUpdate();
        return _executeUpdate();
    }

    @Override
    public int update(Map<String, Object> data) {
        assert (data != null);
        builder.setAll(data);
        return update();
    }

    @Override
    public int delete() {
        builder.buildDelete();
        return _executeUpdate();
    }

    @Override
    public Ar setAll(Map<String, Object> record) {
        builder.setAll(record);
        return this;
    }

    @Override
    public Ar set(String key, Object value) {
        builder.set(key, value);
        return this;
    }

    @Override
    public Ar orWhere(SqlBuilder.Condition condition) {
        builder.orWhere(condition);
        return this;
    }

    @Override
    public Ar where(String key, Object value) {
        builder.where(key, value);
        return this;
    }

    @Override
    public Ar orderBy(String field, SqlBuilder.Sort sort) {
        builder.orderBy(field, sort);
        return this;
    }

    @Override
    public Ar groupBy(String field) {
        builder.groupBy(field);
        return this;
    }

    @Override
    public Ar having(String field, Symbol symbol, Object value) {

        builder.having(field, symbol, value);
        return this;
    }

    @Override
    public Ar union(SqlBuilder sqlBuilder) {
        return null;
    }

    @Override
    public Ar unionAll(SqlBuilder sqlBuilder) {
        return null;
    }

    @Override
    public Ar select(String select) {
        builder.select(select);
        return this;
    }

    @Override
    public Ar select(Iterable<String> select) {
        builder.select(select);
        return this;
    }

    @Override
    public Ar whereNull(String field) {
        builder.whereNull(field);
        return this;
    }


    @Override
    public List<Record> executeQuery(String sql, Object... args) {
        builder.sql.append(sql);
        Collections.addAll(builder.values, args);
        return query();
    }

    @Override
    public Ar join(String table, String on) {
        this.join(table, on, SqlBuilder.INNER);
        return this;
    }

    @Override
    public Ar join(String table, String on, String type) {
        builder.join(table, on, type);
        return this;
    }

    @Override
    public Ar join(String table, String on, SqlBuilder.JoinType type) {
        return join(table, on, type.value());
    }

    @Override
    public Ar orWhere(String key, Object value) {
        builder.orWhere(key, value);
        return this;
    }

    @Override
    public Ar orLike(String name, SqlBuilder.Like like, Object value) {
        builder.orLike(name, like, value);
        return this;
    }

    @Override
    public Ar whereNotNull(String name) {
        builder.whereNotNull(name);
        return this;
    }


    @Override
    public <E> Ar whereIn(String key, E... values) {
        builder.whereIn(key, values);
        return this;
    }

    @Override
    public Ar like(String name, SqlBuilder.Like like, Object value) {
        builder.like(name, like, value);
        return this;
    }

    @Override
    public Ar whereCondition(String key, Object... values) {
        builder.whereCondition(key, values);
        return this;
    }


    @Override
    public Ar where(String key, Symbol symbol, Object value) {
        builder.where(key, symbol, value);
        return this;
    }

    @Override
    public Ar where(SqlBuilder.Condition condition) {
        builder.where(condition);
        return this;
    }

    @Override
    public Ar selectMax(String field) {
        builder.selectMax(field, field);
        return this;
    }

    @Override
    public Ar selectSum(String field) {
        builder.selectSum(field, field);
        return this;
    }

    @Override
    public int executeUpdate(String sql, Object... args) {
        builder.sql.append(sql);
        Collections.addAll(builder.values, args);
        return _executeUpdate();
    }

    @Override
    public int execute(final String sql) {
        if (output) {
            log.info(sql);
        }
        return execute(new ConnectionExecutor() {
            @Override
            public Integer execute(Connection connection) throws SQLException {
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                    builder.sql.append(sql);
                    return statement.executeUpdate(sql);
                } finally {
                    DaoUtils.close(statement);
                }
            }
        });
    }

    public int _executeUpdate() {
        return execute(new ConnectionExecutor() {
            @Override
            public Integer execute(Connection connection) throws SQLException {
                return BuilderKit.executeUpdate(
                        connection, builder, output
                );
            }
        });
    }

    public int _executeInsert(final Record data, final String[] generatedKeys) {
        return execute(new ConnectionExecutor() {
            @Override
            public Integer execute(Connection connection) throws SQLException {
                return BuilderKit.executeInsert(
                        connection, builder,
                        generatedKeys,
                        data, output
                );
            }
        });
    }

    @Override
    protected void clear() {
        super.clear();
        this.nameAdapter = this.defaultNameAdapter;
    }

    @Override
    public <E> E value(final String key, final Class<E> typeOfE) {
        return execute(new ConnectionExecutor() {
            @Override
            public E execute(Connection connection) throws SQLException {
                return EntitySqlUtils.getValue(connection, builder, key, typeOfE, output);
            }
        });
    }
}
