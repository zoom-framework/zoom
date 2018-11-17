package org.zoomdev.zoom.dao.impl.record;

import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.impl.AbstractDaoTest;
import org.zoomdev.zoom.dao.impl.ZoomDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRecord extends AbstractDaoTest {


    protected void process(final Dao dao){
        dao.builder()
                .dropIfExists("product")
                .createTable("product")
                .add("pro_id").integer().keyPrimary().autoIncement()
                .add("pro_name").string(100).keyIndex().notNull()
                .add("pro_price").number().keyIndex().notNull()
                .add("pro_info").text()
                .add("pro_img").blob()
                .add("pro_count").integer().defaultValue(100)
                .add("tp_id").integer().keyIndex()
                .add("shp_id").string(30).keyIndex()


                .dropIfExists("type")
                .createTable("type")
                .add("tp_id").integer().keyPrimary().autoIncement()
                .add("tp_title").string(100).keyIndex().notNull()
                .add("shp_id").string(30).keyIndex().notNull().keyIndex()


                .dropIfExists("customer")
                .createTable("customer")
                .add("cm_id").integer().keyPrimary().autoIncement()
                .add("cm_account").string(100).keyIndex().notNull()
                .add("create_at").timestamp().defaultFunction("CURRENT_TIMESTAMP")


                .dropIfExists("collection")
                .createTable("collection")
                .add("pro_id").integer().keyPrimary()
                .add("usr_id").integer().keyPrimary()
                .add("c_order").integer()

                .dropIfExists("shop")
                .createTable("shop")
                .add("shp_id").string(30).keyPrimary()
                .add("shp_title").string(100)
                .add("shp_level").integer()
                .add("shp_stars").number()
                .add("shp_sales").integer()

                //订单 , 关键字???
                .dropIfExists("shp_order")
                .createTable("shp_order")
                .add("ord_id").integer().keyPrimary().autoIncement()
                .add("shp_id").string(30).notNull()
                .add("pro_id").integer().notNull()
                .add("ord_count").number().notNull()
                .add("ord_status").integer().defaultValue(0).notNull()
                .add("cm_id").integer().notNull()


                .build();



        final String test_business = "testBusiness";

        Record testRecors = Record.as(
                "shp_id", test_business,
                "shp_title", "测试行家",
                "shp_level", 1,
                "shp_stars", 4.9,
                "shp_sales", 100000
        );
        dao.table("shop").insert(testRecors);
        dao.table("shop").where("shp_id",test_business).delete();

        //       dao.table("shop").insert(testRecors);
        //       dao.table("shop").where("id",test_business).delete();


        final String FIRST_BUSINESS = "firstBusiness";

        Record record = Record.as(
                "shp_id", FIRST_BUSINESS,
                "shp_title", "天下第一家",
                "shp_level", 1,
                "shp_stars", 4.9,
                "shp_sales", 100000
        );
        //商家注册 (add)
        dao.table("shop")
                .insert(record);


        record = Record.as(
                "shp_id", "second",
                "shp_title", "弱弱的第二家",
                "shp_level", 2,
                "shp_stars", 2.9,
                "shp_sales", 100
        );
        //商家注册 (add)
        dao.table("shop")
                .insert(record);

        //verify business
        List<Record> result = dao.table("shop")
                .orderBy("shp_id", SqlBuilder.Sort.DESC).find();
        assertEquals(JSON.stringify(result.get(0)), JSON.stringify(record));

        // 商家编辑信息

        assertEquals(dao.table("shop")
                .where("shp_id", FIRST_BUSINESS)
                .update(
                Record.as(
                        "shp_title", "牛逼的第一家"
                )
        ), 1);

        assertEquals(dao.table("shop")
                .where("shp_id", "找不到这家").update(

                Record.as(

                        "shp_title", "牛逼的第一家"
                )
        ), 0);

        // 是不是真的改了?
        assertEquals(dao.table("shop").select("shp_title").where("shp_id",FIRST_BUSINESS).get(),
                Record.as("shp_title", "牛逼的第一家"));


        Record type = Record.as(
                "shp_id", FIRST_BUSINESS,
                "tp_title", "好吃到爆的饭"
        );
        // 商家编辑分类
        assertEquals(dao.table("type")
                .insert(type,"tp_id"), 1);

        //type中应该有id
        assertEquals(type.getInt("tp_id"), 1);


        type = Record.as(
                "shp_id", FIRST_BUSINESS,
                "tp_title", "高级翅膀"
        );
        // 商家编辑分类
        assertEquals(dao.table("type")
                .insert(type,"tp_id"), 1);

        //type中应该有id
        assertEquals(type.getInt("tp_id"), 2);


        //商家编辑商品 (add/edit)

        assertEquals(dao.table("product")
                .insert(Record.as(
                        "pro_name", "牛肉饭",
                        "tp_id", 1,
                        "shp_id", FIRST_BUSINESS,
                        "pro_price", 50.0,
                        "pro_img", "image binary".getBytes(),
                        "pro_info", "very very long text,好长好长啊a"

                )), 1);

        //买家注册 (add)
        assertEquals(dao.table("customer").insert(Record.as(
                "cm_account", "firstUser"
        )), 1);

        //买家浏览商品 (查询 query)
        Page<Record> page = dao
                .table("product")
                .join("type", "type.tp_id=product.tp_id")
                .join("shop", "shop.shp_id=product.shp_id")
                .like("pro_name",SqlBuilder.Like.MATCH_BOTH,"饭")
                .page(1,30);

        assertTrue(page.getTotal() > 0 );


        List<Record> list = dao
                .table("product")
                .join("type", "type.tp_id=product.tp_id")
                .join("shop", "shop.shp_id=product.shp_id")
                .whereIn("pro_id",1,2).limit(0,30);

        assertTrue(list.size() == 1 );


        Record record1 = dao.table("product").whereNull("pro_img").get();

        Record record2 = dao.table("product").whereNotNull("pro_img").get();



        //买家下单商品 (trans:  update/create 减去库存、并创建订单）

        Executor executor = Executors.newFixedThreadPool(10);

        List<Future> futures = new ArrayList<Future>();
        for(int i=0; i < 10; ++i){
            Future future = ((ExecutorService) executor).submit(new Runnable() {
                @Override
                public void run() {
                    ZoomDao.executeTrans(new Runnable() {
                        @Override
                        public void run() {
                            //随机购买几件商品
                            int count = (int) (Math.random() * 10);
                            if(dao.ar().executeUpdate(
                                    "update product set pro_count=pro_count-? where pro_id=? and pro_count>?",
                                    count,1,count) > 0){
                                dao.table("shp_order")
                                        .insert(Record.as(
                                                "shp_id",FIRST_BUSINESS,
                                                "ord_count",count,
                                                "pro_id",1,
                                                "cm_id",1
                                        ));
                            }else{
                                throw new DaoException("库存不足");
                            }

                        }
                    });
                }
            });

            futures.add(future);
        }

        for(Future future : futures){
            try{
                future.get();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        ((ExecutorService) executor).shutdown();



        //商家发货 (update)

        //买家收货（完成一次交易) (update)

        // 买家查看今日总卖出量（统计）

    }

}
