package org.zoomdev.zoom.dao.builder.oracle;

import org.zoomdev.zoom.dao.DataSourceProvider;
import org.zoomdev.zoom.dao.builder.TestBuilder;
import org.zoomdev.zoom.dao.driver.oracle.OracleConnDescription;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

public class TestOracleBuilder extends TestBuilder {
    @Override
    protected DataSourceProvider getDataSoueceProvoider() {
        DataSourceProvider dataSourceProvider = new DruidDataSourceProvider(
            new OracleConnDescription(
                    "localhost",
                    "xe",
                    "root",
                    "root"
            )
        );
        return dataSourceProvider;
    }
}
