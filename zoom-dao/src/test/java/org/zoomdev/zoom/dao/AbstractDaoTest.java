package org.zoomdev.zoom.dao;

import org.junit.After;
import org.junit.Before;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.impl.ZoomDao;

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
    }


}
