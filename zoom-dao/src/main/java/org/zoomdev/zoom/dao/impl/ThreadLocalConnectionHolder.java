package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.ConnectionExecutor;
import org.zoomdev.zoom.dao.ConnectionHolder;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Trans;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class ThreadLocalConnectionHolder implements ConnectionHolder, Trans {
    protected DataSource dataSource;
    protected Connection connection;
    protected SimpleSqlBuilder builder;

    public ThreadLocalConnectionHolder(
            DataSource dataSource,
            SimpleSqlBuilder builder) {
        this.dataSource = dataSource;
        this.builder = builder;
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
            throw new DaoException(builder.printSql(), e);
        } finally {
            clear();
        }
    }

    protected void clear() {
        builder.clear(true);
        releaseConnection();
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
