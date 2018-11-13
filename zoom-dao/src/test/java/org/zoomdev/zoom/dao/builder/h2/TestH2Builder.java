package org.zoomdev.zoom.dao.builder.h2;

import org.junit.Test;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.utils.PathUtils;
import org.zoomdev.zoom.dao.AbstractDaoTest;
import org.zoomdev.zoom.dao.DataSourceProvider;
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
    public void test(){
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class org.h2.Driver", e);
        }

        String sql = dao.builder()
                .dropIfExists("student")
                .createTable("student")
                .add("id").integer().autoIncement().primaryKey()
                .add("name").string(30).notNull().defaultValue("name")
                .add("sex").integer().defaultValue(0)
                .add("info").clob()
                .add("img").blob()
                .dropIfExists("shp_shop")
                .createTable("shp_shop")
                .add("pro_id").integer().primaryKey().autoIncement()
                .add("pro_name").string(100).index().notNull()
                .add("pro_price").number().index().notNull()
                .add("pro_info").clob()
                .add("pro_img").blob()
                .add("tp_id").integer().index()
                .buildSql();


        System.out.println(sql);

        dao.ar().executeUpdate(sql);

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
