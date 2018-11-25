package org.zoomdev.zoom.dao.driver.h2;

import org.zoomdev.zoom.dao.driver.mysql.MysqlDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;

import java.util.List;
import java.util.Map;

public class H2Driver extends MysqlDriver {

    @Override
    public void insertOrUpdate(StringBuilder sb, List<Object> values, String tableName, Map<String, Object> data, String... unikeys) {
        throw new UnsupportedOperationException("insertOrUpdate is not supported in h2");
    }

    @Override
    public void buildTable(TableBuildInfo table, List<String> sqlList) {
        super.buildTable(table, sqlList);

        buildComment(table,sqlList);
    }

    @Override
    protected void createColumnComment(StringBuilder sb, ColumnMeta columnMeta) {

    }

    @Override
    protected void createTableComment(StringBuilder sb, TableBuildInfo table) {

    }
}
