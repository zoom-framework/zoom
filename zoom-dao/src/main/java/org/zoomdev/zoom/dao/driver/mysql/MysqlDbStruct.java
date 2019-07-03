package org.zoomdev.zoom.dao.driver.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.common.utils.Converter;
import org.zoomdev.zoom.dao.Ar;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.alias.impl.EmptyNameAdapter;
import org.zoomdev.zoom.dao.driver.AbsDbStruct;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.driver.Snapshot;
import org.zoomdev.zoom.dao.driver.impl.ZoomSnapshot;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.ColumnMeta.KeyType;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.*;

public class MysqlDbStruct extends AbsDbStruct implements DbStructFactory {

    private String dbName;

    public MysqlDbStruct(Dao dao, String dbName) {
        super(dao);
        this.dbName = dbName;
    }


    private static final Log log = LogFactory.getLog(MysqlDbStruct.class);


    @Override
    public List<String> getTableNames() {
        return null;
    }

    @Override
    public List<TableNameAndComment> getNameAndComments() {
        List<Record> list = dao.ar().executeQuery(
                "select table_comment as comment,table_name as name from information_schema.tables where table_schema=?",
                dbName);

        List<TableNameAndComment> result = new ArrayList<TableNameAndComment>(list.size());
        for (Record record : list) {
            TableNameAndComment data = new TableNameAndComment();
            data.setName(record.getString("name"));
            data.setComment(record.getString("comment"));
            result.add(data);
        }

        return result;
    }

    @Override
    public Map<String, Collection<String>> getTriggers() {
        return Collections.emptyMap();
    }


    @Override
    public Collection<String> getSequences() {
        return Collections.emptyList();
    }


    protected Ar getAllColumns(Ar ar) {
        return ar.table("information_schema.columns").nameAdapter(EmptyNameAdapter.DEFAULT)
                .select("TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,COLUMN_KEY,EXTRA,COLUMN_COMMENT,COLUMN_DEFAULT")
                .where("table_schema", dbName);
    }

    protected void fill(String table, ColumnMeta columnMeta, Record record) {
        columnMeta.setTable(table);
        columnMeta.setName(record.getString("COLUMN_NAME"));
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

    @Override
    public void fill(TableMeta meta) {
        List<Record> list = dao.ar()
                .nameAdapter(EmptyNameAdapter.DEFAULT)
                .executeQuery(
                        "SELECT TABLE_COMMENT AS COMMENT,TABLE_NAME as NAME from information_schema.tables where table_schema=? AND TABLE_NAME=?",
                        dbName,
                        meta.getName());
        if (list.size() > 0) {
            Record record = list.get(0);
            meta.setComment(record.getString("COMMENT"));
        } else {
            meta.setComment("");
        }

        list = getAllColumns(dao.ar()).where("TABLE_NAME", meta.getName()).find();

        for (Record record : list) {
            String column = record.getString("COLUMN_NAME");
            ColumnMeta columnMeta = meta.getColumn(column);
            if (columnMeta == null) {
                // 没有？不可能
                log.warn("找不到对应的字段:" + column);
                continue;
            }

            fill(meta.getName(), columnMeta, record);
        }


    }


    @Override
    public Snapshot takeSnapshot() {
        List<TableNameAndComment> nameAndComments = getNameAndComments();
        List<Record> allColumns = getAllColumns(dao.ar()).find();
        List<ColumnMeta> columnMetas = CollectionUtils.map(allColumns, new Converter<Record, ColumnMeta>() {
            @Override
            public ColumnMeta convert(Record record) {
                ColumnMeta columnMeta = new ColumnMeta();
                String table = record.getString("TABLE_NAME");
                columnMeta.setTable(table);
                fill(table, columnMeta, record);
                return columnMeta;
            }
        });


        final Map<String, List<ColumnMeta>> treeMap = toTreeMap(columnMetas);

        ///tables
        List<TableMeta> tableMetas = CollectionUtils.map(nameAndComments, new Converter<TableNameAndComment, TableMeta>() {
            @Override
            public TableMeta convert(TableNameAndComment data) {
                List<ColumnMeta> children = treeMap.get(data.getName().toLowerCase());
                if (children == null) {
                    throw new DaoException("找不到对应的表" + data.getName());
                }
                TableMeta tableMeta = new TableMeta();
                tableMeta.setName(data.getName());
                tableMeta.setComment(data.getComment());
                //Comment
                tableMeta.setColumns(children.toArray(new ColumnMeta[children.size()]));
                return tableMeta;
            }
        });


        ZoomSnapshot snapshot = new ZoomSnapshot();
        snapshot.setTables(tableMetas);


        return snapshot;
    }
}
