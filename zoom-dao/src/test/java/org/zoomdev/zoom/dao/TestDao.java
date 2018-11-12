package org.zoomdev.zoom.dao;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.driver.mysql.MysqlConnDescription;
import org.zoomdev.zoom.dao.entities.JoinProductWithType;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.util.List;

public class TestDao extends TestCase {

    DataSourceProvider provider = new DruidDataSourceProvider(
            new MysqlConnDescription(
                    "localhost",
                    13306,
                    "zoom",
                    "root",
                    "root"
            )
    );

    Dao dao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dao = new ZoomDao(provider.getDataSource(), false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Classes.destroy(dao);
    }

    /**
     * 比较一下， 正则表达式缓存和不缓存的速度
     */

    public void testEntityJoin() {


        List<JoinProductWithType> list = dao.ar(JoinProductWithType.class).find();
        list = dao.ar(JoinProductWithType.class).filter("id|title").find();

        System.out.println(JSON.stringify(list));

        JoinProductWithType product = dao.ar(JoinProductWithType.class).where("id",2).get();
        product = dao.ar(JoinProductWithType.class).get(2);


        dao.ar(JoinProductWithType.class);

        System.out.println(JSON.stringify(product));


    }
}
