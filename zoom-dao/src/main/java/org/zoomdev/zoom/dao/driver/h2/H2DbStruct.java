package org.zoomdev.zoom.dao.driver.h2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.common.utils.Converter;
import org.zoomdev.zoom.common.utils.MapUtils;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.alias.impl.ToLowerCaseNameAdapter;
import org.zoomdev.zoom.dao.driver.AbsDbStruct;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.ColumnMeta.KeyType;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.*;

public class H2DbStruct extends AbsDbStruct {

    private String dbName;

    public H2DbStruct(Dao dao, String dbName) {
        super(dao);
        this.dbName = dbName;
    }

    private static final Log log = LogFactory.getLog(H2DbStruct.class);


    @Override
    public Collection<TableNameAndComment> getNameAndComments() {
        List<Record> list = dao.ar()
                .table("information_schema.tables")
                .select("TABLE_NAME as NAME,REMARKS AS COMMENT")
                .where("TABLE_SCHEMA", "PUBLIC")
                .find();


        List<TableNameAndComment> result = new ArrayList<TableNameAndComment>(list.size());
        for (Record record : list) {
            TableNameAndComment data = new TableNameAndComment();
            data.setComment(record.getString("comment"));
            data.setName(record.getString("name").toLowerCase());
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

    @Override
    public Collection<String> getTableNames() {
        return CollectionUtils.map(
                getNameAndComments(),
                new Converter<TableNameAndComment, String>() {
                    @Override
                    public String convert(TableNameAndComment data) {
                        return data.getName();
                    }
                }
        );
    }


    @Override
    public void fill(TableMeta meta) {

        List<Record> list = dao
                .ar()
                .nameAdapter(ToLowerCaseNameAdapter.DEFAULT)
                .executeQuery("select TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,SEQUENCE_NAME,CHARACTER_MAXIMUM_LENGTH,REMARKS,COLUMN_DEFAULT from " +
                        "information_schema.columns where TABLE_SCHEMA=? and TABLE_NAME=?", "PUBLIC", meta.getName().toUpperCase());


        //index
        List<Record> indexes = dao
                .ar()
                .nameAdapter(ToLowerCaseNameAdapter.DEFAULT)
                .executeQuery("select COLUMN_NAME,INDEX_TYPE_NAME from INFORMATION_SCHEMA.indexes where TABLE_NAME=?", meta.getName().toUpperCase());


        Map<String, String> indexesMap = MapUtils
                .toKeyAndLabel(indexes, "column_name", "index_type_name");

        for (Record record : list) {
            String column = record.getString("column_name");
            ColumnMeta columnMeta = meta.getColumn(column);
            if (columnMeta == null) {
                // 没有？不可能
                log.warn("找不到对应的字段:" + column);
                continue;
            }

            columnMeta.setComment(record.getString("remarks"));
            // 常用的
            columnMeta.setAuto(!StringUtils.isEmpty(record.getString("sequence_name")));
            String keyType = indexesMap.get(record.getString("column_name"));
            if (StringUtils.startsWith(keyType, "PRIMARY")) {
                columnMeta.setKeyType(KeyType.PRIMARY);
            } else if (StringUtils.startsWith(keyType, "UNIQUE")) {
                columnMeta.setKeyType(KeyType.UNIQUE);
            }

            columnMeta.setDefaultValue(record.getString("column_default"));
            columnMeta.setNullable(record.getString("is_nullable").equals("YES"));

            columnMeta.setMaxLen(record.getInt("character_maximum_length"));
            columnMeta.setRawType(record.getString("data_type"));

        }

    }


}
