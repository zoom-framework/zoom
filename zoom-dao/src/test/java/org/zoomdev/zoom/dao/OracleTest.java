package org.zoomdev.zoom.dao;

import junit.framework.TestCase;
import org.zoomdev.zoom.dao.driver.oracle.OracleConnDescription;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

public class OracleTest extends TestCase {


    public void test(){
//        hostname: localhost
//        port: 49161
//        sid: xe
//        username: system/sys
//        password: oracle


        DruidDataSourceProvider dataSourceProvider = new DruidDataSourceProvider(
                new OracleConnDescription(
                        "localhost",
                        49161,
                        "xe",
                        "system",
                        "oracle"
                )
        );

        Dao dao = new ZoomDao(dataSourceProvider.getDataSource());

    }
}
