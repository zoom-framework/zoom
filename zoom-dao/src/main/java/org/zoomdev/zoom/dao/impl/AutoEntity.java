package org.zoomdev.zoom.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface AutoEntity {


    /**
     * @param connection
     * @param sql
     * @return
     */
    PreparedStatement prepareInsert(Connection connection, String sql) throws SQLException;

    /**
     * 在插入之后所做操作
     *
     * @param data
     * @param ps
     */
    void afterInsert(Object data, PreparedStatement ps) throws SQLException;
}
