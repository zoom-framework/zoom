package org.zoomdev.zoom.dao.builder.mysql;

import org.zoomdev.zoom.dao.DataSourceProvider;
import org.zoomdev.zoom.dao.builder.TestBuilder;
import org.zoomdev.zoom.dao.driver.mysql.MysqlConnDescription;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

public class TestMysqlBuilder extends TestBuilder {
    @Override
    protected DataSourceProvider getDataSoueceProvoider() {
        DruidDataSourceProvider dataSourceProvider = new DruidDataSourceProvider(
                new MysqlConnDescription(
                        "127.0.0.1",
                        13306,
                        "zoom",
                        "root",
                        "root"

                )
        );

        return dataSourceProvider;
    }
}
