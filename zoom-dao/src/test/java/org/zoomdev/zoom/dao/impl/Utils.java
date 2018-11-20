package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;
import org.zoomdev.zoom.dao.migrations.ZoomDatabaseBuilder;

public class Utils {

    public static void createTables(Dao dao) {
        DatabaseBuilder builder = dao.builder();
        String sql = builder

                .dropIfExists("product")
                .createTable("product")
                .add("pro_id").integer().keyPrimary().autoIncement()
                .add("pro_name").string(100).keyIndex().notNull()
                .add("pro_price").number().keyIndex().notNull()
                .add("pro_info").text()
                .add("pro_thumb").string(200)
                .add("pro_img").blob()
                .add("pro_count").integer().defaultValue(100)
                .add("tp_id").integer().keyIndex()
                .add("shp_id").string(30).keyIndex()
                .add("create_at").timestamp().defaultValue(new DatabaseBuilder.FunctionValue("CURRENT_TIMESTAMP"))

                .dropIfExists("type")
                .createTable("type")
                .add("tp_id").integer().keyPrimary().autoIncement()
                .add("tp_title").string(100).keyIndex().notNull()
                .add("shp_id").string(30).keyIndex().notNull().keyIndex()

                .dropIfExists("customer")
                .createTable("customer")
                .add("cm_id").integer().keyPrimary().autoIncement()
                .add("cm_account").string(100).keyUnique().notNull()
                .add("create_at").timestamp().defaultFunction("CURRENT_TIMESTAMP")
                .add("cm_pwd").string(32)

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
                .add("shp_address").string(100)
                .add("shp_sales").integer()

                //订单 , 关键字???
                .createTable("shp_order")
                .add("ord_id").integer().keyPrimary().autoIncement()
                .add("shp_id").string(30).notNull()
                .add("pro_id").integer().notNull()
                .add("ord_count").number().notNull()
                .add("ord_status").integer().defaultValue(0).notNull()
                .add("cm_id").integer().notNull()

                .buildSql();

        System.out.println(sql);


        builder.build();
    }


    public static void createTestMapNameAdapter(Dao dao) {
        dao.builder()
                .dropIfExists("test_map")
                .createTable("test_map")
                .add("MP_ID").integer().keyPrimary().autoIncement()
                .add("JP_NAME").string(30)
                .add("TP_UID").string(30).keyUnique()
                .build();
    }
}
