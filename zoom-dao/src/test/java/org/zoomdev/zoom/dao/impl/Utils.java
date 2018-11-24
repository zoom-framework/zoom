package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;

public class Utils {



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
