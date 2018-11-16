package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.ConnectionExecutor;
import org.zoomdev.zoom.dao.ConnectionHolder;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Trans;
import org.zoomdev.zoom.dao.utils.DaoUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractRecord implements ConnectionHolder, Trans {
    protected DataSource dataSource;
    protected Connection connection;
    protected SimpleSqlBuilder builder;

    public AbstractRecord(
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
    protected void remove2(List<Object> values){
        if(values.size()==0){
            values.clear();
            return;
        }
        values.remove(values.size()-1);
        values.remove(values.size()-1);
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




    public int count() {
        return value(DaoUtils.SELECT_COUNT, int.class);
    }

    public <E> E value(final String key, final Class<E> typeOfE) {
        return execute(new ConnectionExecutor() {
            @Override
            public E execute(Connection connection) throws SQLException {
                return EntitySqlUtils.getValue(connection,builder, key, typeOfE);
            }
        });
    }

}
