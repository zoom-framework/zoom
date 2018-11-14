package org.zoomdev.zoom.dao.builder.h2;

import org.junit.Test;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.common.utils.PathUtils;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.builder.TestBuilder;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestH2Builder extends TestBuilder {

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



    @Override
    protected DataSourceProvider getDataSoueceProvoider() {
        DruidDataSourceProvider dataSourceProvider = new DruidDataSourceProvider();
        dataSourceProvider.setUrl("jdbc:h2:file:" + getDbFile());
        dataSourceProvider.setPassword("sa");
        dataSourceProvider.setUsername("sa");
        dataSourceProvider.setDriverClassName("org.h2.Driver");
        dataSourceProvider.setInitialSize(10);
        dataSourceProvider.setMaxActive(10);
        return dataSourceProvider;
    }
}
