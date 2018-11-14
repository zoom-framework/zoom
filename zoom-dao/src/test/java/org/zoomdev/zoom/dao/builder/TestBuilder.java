package org.zoomdev.zoom.dao.builder;

import org.junit.Test;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.impl.ZoomDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class TestBuilder extends AbstractDaoTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {

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





        final String FIRST_BUSINESS = "firstBusiness";

        Record record = Record.as(
                "id", FIRST_BUSINESS,
                "title", "天下第一家",
                "level", 1,
                "stars", 4.9,
                "sales", 100000
        );
        //商家注册 (add)
        dao.ar("shop")
                .insert(record);


        record = Record.as(
                "id", "second",
                "title", "弱弱的第二家",
                "level", 2,
                "stars", 2.9,
                "sales", 100
        );
        //商家注册 (add)
        dao.ar("shop")
                .insert(record);

        //verify business
        List<Record> result = dao.ar("shop")
                .orderBy("id", SqlBuilder.Sort.DESC).find();
        assertEquals(JSON.stringify(result.get(0)), JSON.stringify(record));

        // 商家编辑信息

        assertEquals(dao.ar("shop").update(
                Record.as(
                        "id", FIRST_BUSINESS,
                        "title", "牛逼的第一家"
                )
        ), 1);

        assertEquals(dao.ar("shop").update(
                Record.as(
                        "id", "找不到这家",
                        "title", "牛逼的第一家"
                )
        ), 0);

        // 是不是真的改了?
        assertEquals(dao.ar("shop").filter("title").get(FIRST_BUSINESS),
                Record.as("title", "牛逼的第一家"));


        Record type = Record.as(
                "shpId", FIRST_BUSINESS,
                "title", "好吃到爆的饭"
        );
        // 商家编辑分类
        assertEquals(dao.ar("type")
                .insert(type), 1);

        //type中应该有id
        assertEquals(type.getInt("id"), 1);


        type = Record.as(
                "shpId", FIRST_BUSINESS,
                "title", "高级翅膀"
        );
        // 商家编辑分类
        assertEquals(dao.ar("type")
                .insert(type), 1);

        //type中应该有id
        assertEquals(type.getInt("id"), 2);


        //商家编辑商品 (add/edit)

        assertEquals(dao.ar("product")
                .insert(Record.as(
                        "name", "牛肉饭",
                        "tpId", 1,
                        "shpId", FIRST_BUSINESS,
                        "price", 50.0,
                        "img", "image binary".getBytes(),
                        "info", "very very long text,好长好长啊a"

                )), 1);

        //买家注册 (add)
        assertEquals(dao.ar("customer").insert(Record.as(
                "account", "firstUser"
        )), 1);

        //买家浏览商品 (查询 query)
        Page<Record> page = dao
                .ar("product", "type", "shop")
                .join("type", "typeId=tpId")
                .join("shop", "shpId=shpId")
                .like("name",SqlBuilder.Like.MATCH_BOTH,"饭")
                .page(1,30);

        assertTrue(page.getTotal() > 0 );


        List<Record> list = dao
                .ar("product", "type", "shop")
                .join("type", "typeId=tpId")
                .join("shop", "shpId=shpId")
                .whereIn("id",1,2).limit(0,30);

        assertTrue(list.size() == 2 );


        Record record1 = dao.ar("product").whereNull("img").get();

        Record record2 = dao.ar("product").whereNotNull("img").get();



        //买家下单商品 (trans:  update/create 减去库存、并创建订单）

        Executor executor = Executors.newFixedThreadPool(10);

        List<Future> futures = new ArrayList<Future>();
        for(int i=0; i < 100; ++i){
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
                                dao.ar("shp_order")
                                        .insert(Record.as(
                                                "shpId",FIRST_BUSINESS,
                                                "count",count,
                                                "proId",1,
                                                "cmId",1
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
