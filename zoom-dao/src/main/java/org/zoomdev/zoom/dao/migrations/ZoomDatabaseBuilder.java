package org.zoomdev.zoom.dao.migrations;

import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.ColumnIgnore;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ZoomDatabaseBuilder implements DatabaseBuilder {



    private Dao dao;


    private List<TableBuildInfo> tables = new ArrayList<TableBuildInfo>();

    private SqlDriver driver;

    private List<BuildInfo> buildInfos = new ArrayList<BuildInfo>();

    private static abstract class BuildInfo {
        abstract void build(StringBuilder sb);
    }

    private class DropTableIfExists extends BuildInfo {

        private String table;

        public DropTableIfExists(String table) {
            this.table = table;
        }

        @Override
        void build(StringBuilder sb) {
            sb.append(String.format("DROP TABLE IF EXISTS %s;\n", table));
        }
    }

    private class CreateTable extends BuildInfo {

        private TableBuildInfo table;


        public CreateTable(TableBuildInfo table) {
            this.table = table;
        }

        @Override
        void build(StringBuilder sb) {
            driver.build(table,sb);
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
        StringBuilder sb = new StringBuilder();

        for(BuildInfo buildInfo : buildInfos){
            buildInfo.build(sb);
        }

        return sb.toString();
    }

    @Override
    public void build() {

        dao.ar().executeQuery(buildSql());
    }

    @Override
    public void build(Class<?> type, boolean dropIfExists) {
        assert (type != null);
        Field[] fields = CachedClasses.getFields(type);
        if (fields.length == 0) {
            throw new DaoException("必须至少有一个字段");
        }

        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if (field.isAnnotationPresent(ColumnIgnore.class)) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                continue;
            }


        }


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
}
