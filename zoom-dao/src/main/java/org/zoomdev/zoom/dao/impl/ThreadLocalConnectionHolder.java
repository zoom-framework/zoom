package org.zoomdev.zoom.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.zoomdev.zoom.dao.ConnectionExecutor;
import org.zoomdev.zoom.dao.ConnectionHolder;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Trans;

public abstract class ThreadLocalConnectionHolder implements ConnectionHolder, Trans {
	private DataSource dataSource;
	private Connection connection;

	public ThreadLocalConnectionHolder(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void beginTransaction(int level) {
		ZoomDao.beginTrans(level);
	}

	@Override
	public void commit() {
		ZoomDao.commitTrans();
	}

	@Override
	public void rollback() {
		ZoomDao.rollbackTrans();
	}

	public Connection getConnection() {
		final Connection connection = this.connection;
		return connection == null ? (this.connection = ZoomDao.getConnection(dataSource)) : connection;
	}

	@Override
	public <T> T execute(ConnectionExecutor executor) {
		try {
			return executor.execute(getConnection());
		} catch (SQLException e) {
			throw new DaoException(printSql(),e);
		} finally {
			releaseConnection();
		}
	}

	protected abstract String printSql();


	public void releaseConnection() {
		final Connection connection = this.connection;
		if (connection != null) {
			try {
				ZoomDao.releaseConnection(dataSource, connection);
			} catch (Throwable e) {

			} finally {
				this.connection = null;
			}
		}
	}
}
