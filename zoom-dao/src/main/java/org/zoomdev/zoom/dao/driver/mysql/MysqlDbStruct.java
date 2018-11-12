package org.zoomdev.zoom.dao.driver.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.driver.AbsDbStruct;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.ColumnMeta.KeyType;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MysqlDbStruct extends AbsDbStruct implements DbStructFactory {

    private String dbName;

    public MysqlDbStruct(Dao dao, String dbName) {
        super(dao);
        this.dbName = dbName;
    }


    private static final Log log = LogFactory.getLog(MysqlDbStruct.class);


    @Override
    public Collection<String> getTableNames() {
        return null;
    }

    @Override
    public Collection<TableNameAndComment> getNameAndComments() {
        List<Record> list = dao.ar().executeQuery(
                "select table_comment as comment,table_name as name from information_schema.tables where table_schema=?",
                dbName);

        List<TableNameAndComment> result = new ArrayList<TableNameAndComment>(list.size());
        for (Record record : list) {
            TableNameAndComment data = Caster.to(record, TableNameAndComment.class);
            result.add(data);
        }

        return result;
    }

    private static Class<?> convertType(String type) {
        if (type.equals("varchar")) {
            return String.class;
        } else if (type.equals("int")) {
            return Integer.class;
        } else if (type.contains("text")) {
            return String.class;
        } else if (type.contains("blob")) {
            return Blob.class;
        } else if (type.contains("date")) {
            return java.sql.Date.class;
        } else if (type.equals("datetime")) {
            return java.sql.Date.class;
        } else if (type.equals("time")) {
            return java.sql.Time.class;
        } else if (type.equals("double")) {
            return Double.class;
        } else if (type.equals("tinyint")) {
            return Boolean.class;
        } else if (type.equals("smallint")) {
            return Short.class;
        } else if (type.equals("mediumint")) {
            return Integer.class;
        } else if (type.equals("bigint")) {
            return Long.class;
        } else if (type.equals("decimal")) {
            return Double.class;
        } else if (type.equals("timestamp")) {
            return Integer.class;
        } else if (type.equals("year")) {
            return Integer.class;
        } else if (type.equals("char")) {
            return String.class;
        }
        throw new RuntimeException("Not supported type " + type);
    }


    @Override
    public void fill(TableMeta meta) {
        List<Record> list = dao.ar().executeQuery(
                "SELECT TABLE_COMMENT AS COMMENT,TABLE_NAME as NAME from information_schema.tables where table_schema=? AND TABLE_NAME=?",
                dbName,
                meta.getName());
        if (list.size() > 0) {
            Record record = list.get(0);
            meta.setComment(record.getString("COMMENT"));
        } else {
            meta.setComment("");
        }

        list = dao.ar().executeQuery(
                "SELECT TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,COLUMN_KEY,EXTRA,COLUMN_COMMENT,COLUMN_DEFAULT FROM information_schema.columns WHERE table_schema=? and TABLE_NAME=?",
                dbName, meta.getName());

        for (Record record : list) {
            String column = record.getString("COLUMN_NAME");
            ColumnMeta columnMeta = meta.getColumn(column);
            if (columnMeta == null) {
                // 没有？不可能
                log.warn("找不到对应的字段:" + column);
                continue;
            }

            columnMeta.setComment(record.getString("COLUMN_COMMENT"));
            // 常用的
            columnMeta.setAuto(record.getString("EXTRA").equals("auto_increment"));
            String key = record.getString("COLUMN_KEY");
            if (key.equals("PRI")) {
                columnMeta.setKeyType(KeyType.PRIMARY);
            } else if (key.equals("UNI")) {
                columnMeta.setKeyType(KeyType.UNIQUE);
            } else if (key.equals("MUL")) {
                columnMeta.setKeyType(KeyType.INDEX);
            }
            columnMeta.setDefaultValue(record.getString("COLUMN_DEFAULT"));
            columnMeta.setNullable(record.getString("IS_NULLABLE").equals("YES"));
            columnMeta.setMaxLen(record.getInt("CHARACTER_MAXIMUM_LENGTH"));
            columnMeta.setRawType(record.getString("DATA_TYPE"));


        }

    }

}
