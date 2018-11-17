package org.zoomdev.zoom.dao.impl;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.PathUtils;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DataSourceProvider;
import org.zoomdev.zoom.dao.driver.oracle.OracleConnDescription;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.io.File;

import static org.junit.Assert.assertEquals;

public abstract class AbstractDaoTest {


    private static final int H2 = 0;
    private static final int MYSQL = 1;
    private static final int ORACLE = 2;

    @Inject(config = "zoom.h2")
    private String dbFile;

    private String getWebInf() {
        File file = PathUtils.getWebInfPath("data");

        if (new File(file, "admin.h2.db").exists()) {
            return file.getAbsolutePath() + "/admin";
        }

        return null;

    }

    private String getDbFile() {
        String dbFile = this.dbFile;
        if (dbFile == null) {
            dbFile = getWebInf();
        } else {
            if (!new File(dbFile + ".h2.db").exists()) {
                dbFile = getWebInf();
            } else {
                dbFile = null;
            }
        }
        if (dbFile == null) {
            dbFile = "./admin";
        }
        return dbFile;
    }



    protected DataSourceProvider getDataSoueceProvoider(int index) {
        switch (index){
            case H2:
            {
                DruidDataSourceProvider dataSourceProvider = new DruidDataSourceProvider();
                dataSourceProvider.setUrl("jdbc:h2:file:" + getDbFile());
                dataSourceProvider.setPassword("sa");
                dataSourceProvider.setUsername("sa");
                dataSourceProvider.setDriverClassName("org.h2.Driver");
                dataSourceProvider.setInitialSize(10);
                dataSourceProvider.setMaxActive(10);
                return dataSourceProvider;
            }
            case MYSQL:
            {
                DataSourceProvider dataSourceProvider = new DruidDataSourceProvider(
                        new OracleConnDescription(
                                "localhost",
                                1521,
                                "xe",
                                "root",
                                "root"
                        )
                );
                return dataSourceProvider;
            }
            case ORACLE:
            {
                DataSourceProvider dataSourceProvider = new DruidDataSourceProvider(
                        new OracleConnDescription(
                                "localhost",
                                1521,
                                "xe",
                                "root",
                                "root"
                        )
                );
                return dataSourceProvider;
            }
        }
        return null;
    }


    @Test
    public void testProgress(){

        for(int i=0; i< 3; ++i){
            Dao dao = new ZoomDao(getDataSoueceProvoider(i).getDataSource());

            process(dao);

            Classes.destroy(dao);
            //connection count ==0;
            if(dao!=null){
                DruidDataSource dataSource = (DruidDataSource) dao.getDataSource();
                assertEquals (dataSource.getActiveCount(),0);
            }
        }


    }

    protected abstract void process(Dao dao);


}
