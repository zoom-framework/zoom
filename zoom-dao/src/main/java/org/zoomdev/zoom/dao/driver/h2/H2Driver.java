package org.zoomdev.zoom.dao.driver.h2;

import org.zoomdev.zoom.dao.driver.mysql.MysqlDriver;

import java.util.List;
import java.util.Map;

public class H2Driver extends MysqlDriver {

    @Override
    public void insertOrUpdate(StringBuilder sb, List<Object> values, String tableName, Map<String, Object> data, String... unikeys) {
        throw new UnsupportedOperationException("insertOrUpdate is not supported in h2");
    }
}
