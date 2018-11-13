package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.ColumnIgnore;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ZoomDatabaseBuilder implements DatabaseBuilder {


    private static class TableMeta {

        boolean createWhenNotExists = false;

        String comment;
        String name;
        List<ColumnMeta> columns = new ArrayList<ColumnMeta>();
    }

    private Dao dao;


    private List<TableMeta> tables = new ArrayList<TableMeta>();

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

        private TableMeta table;


        public CreateTable(TableMeta table) {
            this.table = table;
        }

        @Override
        void build(StringBuilder sb) {
            sb.append("CREATE TABLE ");
            if (table.createWhenNotExists) {
                sb.append("IF NOT EXISTS ");
            }
            driver.protectTable(sb, table.name);
            sb.append("(\n");
            boolean first = false;
            int index = 0;
            for (ColumnMeta columnMeta : table.columns) {
                sb.append("\t");
                driver.protectColumn(sb, columnMeta.getName());
                sb.append(' ')
                        .append(driver.formatColumnType(columnMeta))
                        .append(columnMeta.isNullable() ? " NULL" : " NOT NULL");
                if (columnMeta.getDefaultValue() != null) {
                    if (columnMeta.getDefaultValue() instanceof String) {
                        sb.append(" DEFAULT '").append(columnMeta.getDefaultValue()).append("'");
                    } else {
                        sb.append(" DEFAULT ").append(columnMeta.getDefaultValue());
                    }
                }
                if(columnMeta.isPrimary()){
                    sb.append(" PRIMARY KEY");
                }
                if(columnMeta.isAuto()){
                    sb.append(" auto_increment".toUpperCase());
                }

                sb.append(" COMMENT").append("'").append(columnMeta.getComment()==null ? "":columnMeta.getComment()).append("'");

                if( index < table.columns.size() - 1){
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='");
            sb.append(table.comment==null ? "":table.comment)
                    .append("'\n\n");
        }
    }

    ZoomDatabaseBuilder(Dao dao) {
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
        tableMeta.createWhenNotExists = true;

        return this;
    }

    @Override
    public DatabaseBuilder comment(String comment) {
        tableMeta.comment = comment;
        return this;
    }

    @Override
    public DatabaseBuilder createTable(String table) {
        tableMeta = new TableMeta();
        tableMeta.name = table;
        tables.add(tableMeta);
        buildInfos.add(new CreateTable(tableMeta));
        return this;
    }


    private TableMeta tableMeta;
    private ColumnMeta columnMeta;

    @Override
    public DatabaseBuilder add(String column) {

        columnMeta = new ColumnMeta();
        tableMeta.columns.add(columnMeta);
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
    public DatabaseBuilder primaryKey() {
        columnMeta.setKeyType(ColumnMeta.KeyType.PRIMARY);
        return this;
    }

    @Override
    public DatabaseBuilder autoIncement() {
        columnMeta.setAuto(true);
        return this;
    }

    @Override
    public DatabaseBuilder unique() {
        columnMeta.setKeyType(ColumnMeta.KeyType.UNIQUE);
        return this;
    }

    @Override
    public DatabaseBuilder index() {
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
}
