package com.jzoom.zoom.dao.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.utils.DaoUtils;

class Transaction {
	int level;
	Integer oldLevel;
	DataSource dataSource;
	Connection connection;

	public Transaction(int level, DataSource dataSource) {
		this.level = level;
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		Connection connection = this.connection;
		if (connection == null) {
			final int level = this.level;
			try {
				connection = dataSource.getConnection();
				connection.setAutoCommit(false);
				int oldLevel = connection.getTransactionIsolation();
				if (oldLevel != level) {
					connection.setTransactionIsolation(level);
					this.oldLevel = oldLevel;
				}
				this.connection = connection;
			} catch (SQLException e) {
				throw new DaoException("在事务处理的时候获取connection失败", e);
			}
		}
		return connection;
	}

	public void commit() {
		final Connection connection = this.connection;
		final Integer oldLevel = this.oldLevel;
		if (connection != null) {
			try {
				connection.commit();
			}catch (SQLException e) {
				throw new DaoException("事务处理commit失败", e);
			} finally {
				try {
					connection.setAutoCommit(true);
					if (oldLevel != null) {
						connection.setTransactionIsolation(oldLevel);
					}
				} catch (SQLException e) {
					throw new DaoException("事务处理commit之后设置失败", e);
				}
			}
			
		}
	}

	public void rollback() {
		final Connection connection = this.connection;
		final Integer oldLevel = this.oldLevel;
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DaoException("事务处理rollback失败", e);
			}finally {
				try {
					connection.setAutoCommit(true);
					if (oldLevel != null) {
						connection.setTransactionIsolation(oldLevel);
					}
				} catch (SQLException e) {
					throw new DaoException("事务处理rollback之后设置", e);
				}
			}
			
		}
	}

	public void clean() {
		DaoUtils.close(connection);
	}
}
