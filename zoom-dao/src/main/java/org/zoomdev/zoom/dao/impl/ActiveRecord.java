package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.adapters.NameAdapter;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.utils.DaoUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.dao.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ActiveRecord extends ThreadLocalConnectionHolder implements Ar, ConnectionHolder, Trans {

	private AliasSqlBuilder builder;
	private Dao dao;

	private static final Log log = LogFactory.getLog(ActiveRecord.class);

	public ActiveRecord(Dao dao) {
		super(dao.getDataSource());
		this.builder = new AliasSqlBuilder(dao);
	}

	public List<Record> query(String sql, List<Object> values, List<StatementAdapter> adapters,
							  boolean all) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			ps = BuilderKit.prepareStatement(connection, sql, values);
			rs = ps.executeQuery();
			return BuilderKit.build(rs, builder.nameAdapter);
		} catch (SQLException e) {
            throw new DaoException(builder.printSql(), e);
		} finally {
			DaoUtils.close(rs);
			DaoUtils.close(ps);
			releaseConnection();
			builder.clear(all);
		}
	}

	public Record get(String sql, List<Object> values, List<StatementAdapter> adapters , NameAdapter nameAdapter) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			ps = BuilderKit.prepareStatement(connection, sql, values);
			rs = ps.executeQuery();
			if (rs.next()) {
				return BuilderKit.buildOne(rs, nameAdapter);
			}
			return null;
		} catch (SQLException e) {
			throw new DaoException(builder.printSql(), e);
		} finally {
			DaoUtils.close(rs);
			DaoUtils.close(ps);
			releaseConnection();
			builder.clear(true);
		}
	}

	//不一致的地方:
	// 返回值，参数，过程，一致的地方： 都是获取connection先
	// 查询 和  更新 两类
	// page 、 list  单个
	//  ResultSet 映射成为Record或者实体类
	// QueryExecutor:

//	public static  class QueryExecutor implements ConnectionExecutor{
//
//	}


	public ResultSet execute(final String sql, final List<Object> values, final List<StatementAdapter> adapters,
			final boolean all) {
		return execute(new ConnectionExecutor() {

			@SuppressWarnings("unchecked")
			@Override
			public ResultSet execute(Connection connection) throws SQLException {
				ResultSet rs = null;
				PreparedStatement ps = null;
				try {
					connection = getConnection();
					ps = BuilderKit.prepareStatement(connection, sql, values);
					rs = ps.executeQuery();
					return rs;
				} catch (SQLException e) {
					throw new DaoException(sql.toString(), e);
				} finally {
					DaoUtils.close(ps);
					builder.clear(all);
				}
			}
		});
	}

	@Override
	public <T> T execute(ConnectionExecutor executor) {
		try {
			return executor.execute(getConnection());
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			releaseConnection();
		}
	}


	@Override
	public List<Record> find() {
		builder.buildSelect();
		return query(builder.sql.toString(), builder.getValues(),new ArrayList<StatementAdapter>(), true);
	}

	@Override
	public Record get() {
		builder.buildSelect();
		return get(builder.sql.toString(), builder.getValues(), builder.adapters, builder.nameAdapter);
	}

	@Override
	public List<Record> limit(int position, int pageSize) {
		builder.buildLimit(position, pageSize);
		return query(builder.sql.toString(), builder.values,new ArrayList<StatementAdapter>(), true);
	}

	@Override
	public Page<Record> page(int page, int pageSize) {
		if (page <= 0)
			page = 1;
		return position((page - 1) * pageSize, pageSize);
	}

	@Override
	public Page<Record> position(int position, int pageSize) {
		builder.buildLimit(position, pageSize);

		try {
			List<Record> list = query(builder.sql.toString(), builder.values,new ArrayList<StatementAdapter>(), false);
			int total = getCount();
			int page = builder.getPageFromPosition(position, pageSize);
			return new Page<Record>(list, page, pageSize, total);
		} finally {
			builder.clear(true);
		}

	}
	@Override
	public Ar tables(String[] arr) {
		builder.tables(arr);
		return this;
	}
	@Override
	public Ar table(String table) {
		builder.table(table);
		return this;
	}



	@Override
	public int insertOrUpdate(String... keys) {
		builder.insertOrUpdate(keys);
		return executeUpdate(builder.sql.toString(), builder.values, builder.adapters);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int insert(Map<String, Object> data) {
		assert (data != null);
		builder.setAll( data);
		builder.buildInsert();
		return executeUpdate(builder.sql.toString(), builder.values, builder.adapters);
	}

	@Override
	public int insert() {
		builder.buildInsert();
		return executeUpdate(builder.sql.toString(), builder.values, builder.adapters);
	}

	@Override
	public int update() {
		builder.buildUpdate();
		return executeUpdate(builder.sql.toString(), builder.values, builder.adapters);
	}

	@Override
	public int update(Map<String, Object> record) {
		builder.buildUpdate(record);
		return executeUpdate(builder.sql.toString(), builder.values, builder.adapters);
	}

	@Override
	public int delete() {
		builder.buildDelete();
		return executeUpdate(builder.sql.toString(), builder.values, builder.adapters);
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
	public Ar select(String select) {
		builder.select(select);
		return this;
	}

	@Override
	public Ar select(Iterable<String> select) {
		builder.select(select);
		return this;
	}

	public Ar selectRaw(String select) {
		builder.selectRaw(select);
		return this;
	}

	@Override
	public List<Record> executeQuery(String sql, Object... args) {
		return query(sql, Arrays.asList(args),new ArrayList<StatementAdapter>(), true);
	}

	@Override
	public Ar join(String table, String on) {
        builder.join(table, on, "INNER");
		return this;
	}

    @Override
    public Ar join(String table, String on, String type) {
        builder.join(table, on, type);
        return this;
    }

	@Override
	public Ar orWhere(String key, Object value) {
		builder.orWhere(key, value);
		return this;
	}

	@Override
	public <T> T getValue(String select, Class<T> classOfT) {
		Record record = select(select).get();
		if (record == null) {
			return Caster.to(null, classOfT);
		}
		String as = BuilderKit.parseAs(select);
		return record.get(as, classOfT);
	}

	public int getCount() {
		Record record = selectRaw("count(*)").get();
		if (record == null) {
			return Caster.to(null, int.class);
		}
		return record.get("count(*)", int.class);
	}

	@Override
	public Ar whereIn(String key, Object... values) {
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
	public Ar selectMax(String field) {
		builder.selectMax(field);
		return this;
	}

	@Override
	public int executeUpdate(String sql, Object... args) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = BuilderKit.prepareStatement(connection, sql, Arrays.asList(args));
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(sql.toString(), e);
		} finally {
			DaoUtils.close(ps);
			releaseConnection();
			builder.clear(true);
		}
	}

	public int executeUpdate(String sql, List<Object> values, List<StatementAdapter> adapters) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = BuilderKit.prepareStatement(connection, sql, values);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(sql.toString(), e);
		} finally {
			DaoUtils.close(ps);
			releaseConnection();
			builder.clear(true);
		}
	}


}