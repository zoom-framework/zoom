package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.StrKit;
import org.zoomdev.zoom.dao.BeanTableInfo;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.annotations.AutoGenerate;
import org.zoomdev.zoom.dao.annotations.ColumnIgnore;
import org.zoomdev.zoom.dao.annotations.PrimaryKey;
import org.zoomdev.zoom.dao.annotations.UniqueKey;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        if (columnMeta != null) {
            columnMeta.setComment(comment);

        } else if (tableBuildInfo != null) {
            tableBuildInfo.comment = comment;
        }

        return this;
    }

    @Override
    public DatabaseBuilder createTable(String table) {
        tableBuildInfo = new TableBuildInfo();
        tableBuildInfo.name = table;
        tables.add(tableBuildInfo);
        buildInfos.add(new CreateTable(tableBuildInfo));

        columnMeta = null;
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
    public DatabaseBuilder nstring(int len) {
        columnMeta.setType(Types.NVARCHAR);
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
            if (!sql.endsWith(";")) {
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

        CachedEntityFactory cachedEntityFactory = (CachedEntityFactory)dao.getEntityFactory();
        BeanTableInfo beanTableInfo = cachedEntityFactory.getBeanEntityFactory().getTableAdapter().getTableInfo(type);
        //build this table
        String table = beanTableInfo.getTableNames()[0];
        if(dropIfExists){
            this.dropIfExists(table);
            createTable(table);
        }else{
            createIfNotExists(table);
        }



        //fields

        Field[] fields = CachedClasses.getFields(type);
        for(Field field : fields){

            if(field.isAnnotationPresent(ColumnIgnore.class)){
                continue;
            }
            String columnName = StrKit.toUnderLine(field.getName());
            add(columnName);
            Class<?> fieldType= field.getType();
            if(Classes.isString(fieldType)){
                string(200);
            }else if(Classes.isBoolean(fieldType)){
                integer();
            }else if(Classes.isInteger(fieldType)){
                integer();
            }else if(Classes.isNumber(fieldType)){
                number();
            }else if(Classes.isDateTime(fieldType)){
                date();
            }else if(Classes.isEnum(fieldType)){
                string(30);
            }else if(Map.class.isAssignableFrom(fieldType)){
                clob();
            }else if(Iterable.class.isAssignableFrom(fieldType)){
                clob();
            }else if(byte[].class.isAssignableFrom(fieldType)){
                blob();
            }else if(File.class.isAssignableFrom(fieldType)){
                blob();
            }else {
                clob();
            }

            if(field.isAnnotationPresent(PrimaryKey.class)){
                keyPrimary();
            }

            if(field.isAnnotationPresent(AutoGenerate.class)){
                keyPrimary();
                autoIncement();
            }

            if(field.isAnnotationPresent(UniqueKey.class)){
                keyUnique();
            }

        }


        build();
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
