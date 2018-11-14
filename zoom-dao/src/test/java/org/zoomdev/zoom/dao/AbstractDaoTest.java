package org.zoomdev.zoom.dao;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.After;
import org.junit.Before;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.impl.ZoomDao;

import static org.junit.Assert.assertEquals;

public abstract class AbstractDaoTest {

    protected Dao dao;

    protected abstract DataSourceProvider getDataSoueceProvoider();

    @Before
    public void setup(){
        dao = new ZoomDao(getDataSoueceProvoider().getDataSource());
    }

    @After
    public void after(){
        Classes.destroy(dao);
        //connection count ==0;
        DruidDataSource dataSource = (DruidDataSource) dao.getDataSource();

        assertEquals (dataSource.getActiveCount(),0);


    }


}
