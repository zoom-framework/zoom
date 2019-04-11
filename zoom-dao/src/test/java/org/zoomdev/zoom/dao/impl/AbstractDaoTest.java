package org.zoomdev.zoom.dao.impl;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;
import org.zoomdev.zoom.http.annotations.Inject;
import org.zoomdev.zoom.http.utils.Classes;
import org.zoomdev.zoom.http.utils.PathUtils;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.DataSourceProvider;
import org.zoomdev.zoom.dao.driver.mysql.MysqlConnDescription;
import org.zoomdev.zoom.dao.driver.oracle.OracleConnDescription;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.io.File;

import static org.junit.Assert.assertEquals;

public abstract class AbstractDaoTest {


    private static final int H2 = 0;
    private static final int MYSQL = 1;
    private static final int ORACLE = 2;
    private static final int MAX = 2;
    @Inject(config = "zoom.h2")
    private static String dbFile;

    private static String getWebInf() {
        File file = PathUtils.getWebInfPath("data");

        if (new File(file, "admin.h2.db").exists()) {
            return file.getAbsolutePath() + "/admin";
        }

        return null;

    }

    private static String getDbFile() {
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


    protected static DataSourceProvider getDataSoueceProvoider(int index) {
        switch (index) {
            case H2: {
                DruidDataSourceProvider dataSourceProvider = new DruidDataSourceProvider();
                dataSourceProvider.setUrl("jdbc:h2:file:" + getDbFile());
                dataSourceProvider.setPassword("sa");
                dataSourceProvider.setUsername("sa");
                dataSourceProvider.setDriverClassName("org.h2.Driver");
                dataSourceProvider.setInitialSize(10);
                dataSourceProvider.setMaxActive(10);
                return dataSourceProvider;
            }
            case MYSQL: {
                DataSourceProvider dataSourceProvider = new DruidDataSourceProvider(
                        new MysqlConnDescription(
                                "localhost",
                                3306,
                                "zoom",
                                "root",
                                "root"
                        )
                );
                return dataSourceProvider;
            }
            case ORACLE: {
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
    public void testProgress() {
        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                process(dao);
            }
        });
    }


    protected static interface RunWithDao {
        void run(Dao dao) throws Exception;
    }


    protected static void execute(RunWithDao runWithDao) {
        for (int i = 0; i < MAX; ++i) {
            Dao dao = new ZoomDao(getDataSoueceProvoider(i).getDataSource());

            try {
                runWithDao.run(dao);
            } catch (Exception e) {

                System.err.println(dao.getURL());
                e.printStackTrace();

                if(e instanceof DaoException){
                    throw (DaoException)e;
                }
                throw new DaoException(e);
            }

            Classes.destroy(dao);
            //connection count ==0;
            if (dao != null) {
                DruidDataSource dataSource = (DruidDataSource) dao.getDataSource();
                assertEquals(dataSource.getActiveCount(), 0);
            }
        }
    }

    protected abstract void process(Dao dao);


}
