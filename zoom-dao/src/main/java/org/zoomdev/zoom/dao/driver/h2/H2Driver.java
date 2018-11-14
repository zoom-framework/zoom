package org.zoomdev.zoom.dao.driver.h2;

import org.zoomdev.zoom.dao.driver.mysql.MysqlDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;
import org.zoomdev.zoom.dao.migrations.ZoomDatabaseBuilder;

import java.util.ArrayList;
import java.util.List;

public class H2Driver extends MysqlDriver {


    @Override
    public String getTableCatFromUrl(String url) {
        return url.substring(url.lastIndexOf("/")+1).toUpperCase();
    }
}
