package com.jzoom.zoom.dao.impl;

import java.sql.Connection;

import javax.sql.DataSource;

import com.jzoom.zoom.dao.ConnectionHolder;
import com.jzoom.zoom.dao.Trans;

public class ThreadLocalConnectionHolder implements ConnectionHolder, Trans {
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
