package org.zoomdev.zoom.dao.builder.h2;

import org.junit.Test;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.utils.PathUtils;
import org.zoomdev.zoom.dao.AbstractDaoTest;
import org.zoomdev.zoom.dao.DataSourceProvider;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestH2Builder extends AbstractDaoTest {

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


    @Test
    public void test() {

        String sql = dao.builder()
                .dropIfExists("product")
                .createTable("product")
                .add("pro_id").integer().keyPrimary().autoIncement()
                .add("pro_name").string(100).keyIndex().notNull()
                .add("pro_price").number().keyIndex().notNull()
                .add("pro_info").text()
                .add("pro_img").blob()
                .add("tp_id").integer().keyIndex()
                .add("shp_id").integer().keyIndex()


                .dropIfExists("type")
                .createTable("type")
                .add("tp_id").integer().keyPrimary().autoIncement()
                .add("tp_title").string(100).keyIndex().notNull()
                .add("shp_id").string(30).keyIndex().notNull()
                .keyIndex()


                .dropIfExists("collection")
                .createTable("collection")
                .add("pro_id").integer().keyPrimary()
                .add("usr_id").integer().keyPrimary()
                .add("order").integer()

                .dropIfExists("shop")
                .createTable("shop")
                .add("shp_id").string(30).keyPrimary()
                .add("shp_title").string(100)
                .add("shp_level").integer()
                .add("shp_stars").number()
                .add("shp_sales").integer()
                .buildSql();


        System.out.println(sql);

        dao.ar().executeUpdate(sql);


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
        List<Record> result = dao.ar("business").orderBy("id", SqlBuilder.Sort.DESC).find();
        assertEquals(result.get(0), record);

        // 商家编辑信息

        assertEquals(dao.ar("shop").update(
                Record.as(
                        "id", FIRST_BUSINESS,
                        "title", "牛逼的第一家"
                )
        ),1);

        assertEquals(dao.ar("shop").update(
                Record.as(
                        "id", "找不到这家",
                        "title", "牛逼的第一家"
                )
        ),0);

        // 是不是真的改了?
        assertEquals(dao.ar("shop").filter("title").get(FIRST_BUSINESS),
                Record.as("title","牛逼的第一家"));


        // 商家编辑分类
        assertEquals(dao.ar("type")
                .insert(Record.as(
                        "shpId", FIRST_BUSINESS,
                        "title","好吃到爆的饭"
                        )),1);



        //商家编辑商品 (add/edit)

        dao.ar("product")
                .insert(Record.as(
                        "id", FIRST_BUSINESS
                ));

        //买家注册 (add)

        //买家浏览商品 (查询 query)

        //买家收藏商品 (增加)

        //买家查看收藏夹(联合查询）

        //买家下单商品 (trans:  update/create 减去库存、并创建订单）

        //商家发货 (update)

        //买家收货（完成一次交易) (update)

        // 买家查看今日总卖出量（统计）


    }

    @Override
    protected DataSourceProvider getDataSoueceProvoider() {
        DruidDataSourceProvider dataSourceProvider = new DruidDataSourceProvider();
        dataSourceProvider.setUrl("jdbc:h2:file:" + getDbFile());
        dataSourceProvider.setPassword("sa");
        dataSourceProvider.setUsername("sa");
        dataSourceProvider.setDriverClassName("org.h2.Driver");
        return dataSourceProvider;
    }
}
