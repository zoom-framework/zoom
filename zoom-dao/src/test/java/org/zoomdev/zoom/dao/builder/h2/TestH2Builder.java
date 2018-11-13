package org.zoomdev.zoom.dao.builder.h2;

import org.junit.Test;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.utils.PathUtils;
import org.zoomdev.zoom.dao.AbstractDaoTest;
import org.zoomdev.zoom.dao.DataSourceProvider;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.io.File;

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

                .dropIfExists("collection")
                .createTable("collection")
                .add("pro_id").integer().keyPrimary()
                .add("usr_id").integer().keyPrimary()
                .add("order").integer()

                .dropIfExists("business")
                .createTable("business")
                .add("bs_id").string(30).keyPrimary()
                .add("bs_title").string(100)
                .add("bs_level").integer()
                .add("bs_starts").number()
                .add("bs_sales").integer()
                .buildSql();


        System.out.println(sql);

        dao.ar().executeUpdate(sql);

        Record record = Record.as(
                "id", "firstBusiness",
                "title", "天下第一家",
                "level",1,
                "stars",4.9,
                "sales",100000
        );
        //商家注册 (add)
        dao.ar("business")
                .insert(record);


        //商家编辑商品 (add/edit)

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
