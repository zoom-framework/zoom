package org.zoomdev.zoom.dao;

import junit.framework.TestCase;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.zoomdev.zoom.common.codec.Hex;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.driver.mysql.MysqlConnDescription;
import org.zoomdev.zoom.dao.entities.JoinProductWithType;
import org.zoomdev.zoom.dao.entities.SimpleProduct;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.util.Arrays;
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

//    public void testEntityJoin() {
//
//
//        List<JoinProductWithType> list = dao.ar(JoinProductWithType.class).find();
//        list = dao.ar(JoinProductWithType.class).filter("id|title").find();
//
//        System.out.println(JSON.stringify(list));
//
//        JoinProductWithType product = dao.ar(JoinProductWithType.class).where("id",2).get();
//        product = dao.ar(JoinProductWithType.class).get(2);
//
//
//
//
//    }

    public void testSimpleEntityQuery(){

        // query
        List<SimpleProduct> products = dao.ar(SimpleProduct.class)
                .like("name",SqlBuilder.Like.MATCH_START,"张")
                .find();

        products = dao.ar(SimpleProduct.class).where("id",2).find();

        products = dao.ar(SimpleProduct.class)
                .orderBy("id", SqlBuilder.Sort.DESC).find();

        products = dao.ar(SimpleProduct.class)
                .limit(1,30);

        Page<SimpleProduct> page = dao.ar(SimpleProduct.class)
                .page(1,30);


    }

    public void testSimpleEntityModify(){

        SimpleProduct simpleProduct = new SimpleProduct();
        simpleProduct.setName("测试商品");
        simpleProduct.setPrice(100);
        simpleProduct.setInfo("介绍");
        simpleProduct.setThumb("Http://mycom/1.jpg");
        simpleProduct.setImg("测试图片src".getBytes());

        dao.ar(SimpleProduct.class)
                .insert(simpleProduct);

        assertFalse(simpleProduct.getId() == 0);



        SimpleProduct queryProduct = dao.ar(SimpleProduct.class).get(simpleProduct.getId());

        assertEquals(queryProduct.getId(),simpleProduct.getId());
        assertEquals(queryProduct.getName(),simpleProduct.getName());
        assertEquals(queryProduct.getPrice(),simpleProduct.getPrice());
        assertEquals(queryProduct.getInfo(),simpleProduct.getInfo());
        assertEquals(queryProduct.getThumb(),simpleProduct.getThumb());

       assertTrue( Arrays.equals(queryProduct.getImg(),simpleProduct.getImg()));

       queryProduct.setThumb("http://test.com/2.jpg");
       queryProduct.setName("修改名称");

        //值修改一个字段
       dao.ar(SimpleProduct.class)
               .filter("thumb")
               .update(queryProduct);


        queryProduct = dao.ar(SimpleProduct.class).get(simpleProduct.getId());
        assertEquals(queryProduct.getId(),simpleProduct.getId());
        assertEquals(queryProduct.getName(),simpleProduct.getName());
        assertEquals(queryProduct.getPrice(),simpleProduct.getPrice());
        assertEquals(queryProduct.getInfo(),simpleProduct.getInfo());
        assertFalse(
                ObjectUtils.equals(queryProduct.getThumb(),simpleProduct.getThumb())
        );
    }

}
