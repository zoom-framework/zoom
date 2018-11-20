package org.zoomdev.zoom.dao.migrations;

import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.meta.ColumnMeta;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ZoomDatabaseBuilder implements DatabaseBuilder {


    private Dao dao;


    private List<TableBuildInfo> tables = new ArrayList<TableBuildInfo>();

    private SqlDriver driver;

    private List<BuildInfo> buildInfos = new ArrayList<BuildInfo>();

    private static abstract class BuildInfo {
        abstract void build(List<String> sqls);
    }



    private class DropTableIfExists extends BuildInfo {

        private String table;

        public DropTableIfExists(String table) {
            this.table = table;
        }

        @Override
        void build(List<String> sqls) {

            sqls.add(driver.buildDropIfExists(table));


        }
    }

    private class CreateTable extends BuildInfo {

        private TableBuildInfo table;


        public CreateTable(TableBuildInfo table) {
            this.table = table;
        }

        @Override
        void build(List<String> sqls) {
            driver.buildTable(table, sqls);
        }
    }

    public ZoomDatabaseBuilder(Dao dao) {
        this.dao = dao;
        this.driver = dao.getDriver();
    }

    @Override
    public DatabaseBuilder dropIfExists(String table) {
        buildInfos.add(new DropTableIfExists(table));
        return this;
    }

    @Override
    public DatabaseBuilder createIfNotExists(String table) {
        createTable(table);
        tableBuildInfo.createWhenNotExists = true;

        return this;
    }

    @Override
    public DatabaseBuilder comment(String comment) {
        tableBuildInfo.comment = comment;
        return this;
    }

    @Override
    public DatabaseBuilder createTable(String table) {
        tableBuildInfo = new TableBuildInfo();
        tableBuildInfo.name = table;
        tables.add(tableBuildInfo);
        buildInfos.add(new CreateTable(tableBuildInfo));
        return this;
    }


    private TableBuildInfo tableBuildInfo;
    private ColumnMeta columnMeta;

    @Override
    public DatabaseBuilder add(String column) {

        columnMeta = new ColumnMeta();
        tableBuildInfo.columns.add(columnMeta);
        columnMeta.setNullable(true);
        columnMeta.setName(column);

        return this;
    }

    @Override
    public DatabaseBuilder modify(String table, String column) {
        return this;
    }

    @Override
    public DatabaseBuilder string(int len) {
        columnMeta.setType(Types.VARCHAR);
        columnMeta.setMaxLen(len);
        return this;
    }


    @Override
    public DatabaseBuilder text() {
        columnMeta.setType(Types.CLOB);
        return this;
    }

    @Override
    public DatabaseBuilder timestamp() {
        columnMeta.setType(Types.TIMESTAMP);
        return this;
    }

    @Override
    public DatabaseBuilder date() {
        columnMeta.setType(Types.DATE);
        return this;
    }

    @Override
    public DatabaseBuilder integer() {
        columnMeta.setType(Types.INTEGER);
        return this;
    }

    @Override
    public DatabaseBuilder bigInt() {
        columnMeta.setType(Types.BIGINT);
        return this;
    }

    @Override
    public DatabaseBuilder clob() {
        columnMeta.setType(Types.CLOB);
        return this;
    }

    @Override
    public DatabaseBuilder number() {
        columnMeta.setType(Types.NUMERIC);
        return this;
    }

    @Override
    public DatabaseBuilder notNull() {
        columnMeta.setNullable(false);
        return this;
    }

    @Override
    public DatabaseBuilder keyPrimary() {
        columnMeta.setKeyType(ColumnMeta.KeyType.PRIMARY);
        return this;
    }

    @Override
    public DatabaseBuilder autoIncement() {
        columnMeta.setAuto(true);
        return this;
    }

    @Override
    public DatabaseBuilder keyUnique() {
        columnMeta.setKeyType(ColumnMeta.KeyType.UNIQUE);
        return this;
    }

    @Override
    public DatabaseBuilder keyIndex() {
        columnMeta.setKeyType(ColumnMeta.KeyType.INDEX);
        return this;
    }

    @Override
    public String buildSql() {
        List<String> list = new ArrayList<String>();


        for (BuildInfo buildInfo : buildInfos) {
            buildInfo.build(list);
        }



        StringBuilder sb = new StringBuilder();

        for (String sql : list) {
            sb.append(sql);
            if(!sql.endsWith(";")){
                sb.append(";");
            }
            sb.append("\n");
        }

        return sb.toString();

    }

    @Override
    public void build() {
        final List<String> list = new ArrayList<String>();
        ZoomDao.executeTrans(new Runnable() {
            @Override
            public void run() {
                for (BuildInfo buildInfo : buildInfos) {
                    buildInfo.build(list);
                    for (String str : list) {
                        dao.ar().execute(str);
                    }

                    list.clear();
                }
            }
        });


    }

    @Override
    public void build(Class<?> type, boolean dropIfExists) {
        assert (type != null);

        //dao.getEntityFactory();


    }

    @Override
    public DatabaseBuilder defaultValue(Object value) {
        columnMeta.setDefaultValue(value);
        return this;
    }

    @Override
    public DatabaseBuilder blob() {
        columnMeta.setType(Types.BLOB);
        return this;
    }

    @Override
    public DatabaseBuilder defaultFunction(String value) {
        columnMeta.setDefaultValue(new FunctionValue(value));
        return this;
    }


}
