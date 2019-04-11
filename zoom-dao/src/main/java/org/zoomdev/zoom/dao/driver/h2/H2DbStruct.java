package org.zoomdev.zoom.dao.driver.h2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.http.utils.CollectionUtils;
import org.zoomdev.zoom.http.utils.Converter;
import org.zoomdev.zoom.dao.Ar;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.alias.impl.ToLowerCaseNameAdapter;
import org.zoomdev.zoom.dao.driver.AbsDbStruct;
import org.zoomdev.zoom.dao.driver.Snapshot;
import org.zoomdev.zoom.dao.driver.impl.ZoomSnapshot;
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
    public List<TableNameAndComment> getNameAndComments() {
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
    public List<String> getTableNames() {
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


    protected Ar getAllColumns(Ar ar){
        return ar.table("information_schema.columns")
                .nameAdapter(ToLowerCaseNameAdapter.DEFAULT)
                .select("TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,SEQUENCE_NAME,CHARACTER_MAXIMUM_LENGTH,REMARKS,COLUMN_DEFAULT")
                .where("TABLE_SCHEMA","PUBLIC");
    }

    protected Ar getAllIndexes(Ar ar){
        return ar.table("INFORMATION_SCHEMA.indexes")
                .nameAdapter(ToLowerCaseNameAdapter.DEFAULT)
                .select("TABLE_NAME,COLUMN_NAME,INDEX_TYPE_NAME");
    }

    protected void fill( ColumnMeta columnMeta, Record record, Map<String, String> indexesMap) {
        columnMeta.setName(record.getString("column_name"));
        columnMeta.setComment(record.getString("remarks"));
        // 常用的
        columnMeta.setAuto(!StringUtils.isEmpty(record.getString("sequence_name")));
        String keyType = indexesMap.get(getRecordKey(record));
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
    @Override
    public void fill(TableMeta meta) {


        // columns
        List<Record> list = getAllColumns(dao.ar()).where("TABLE_NAME",meta.getName().toUpperCase()).find();
        //index
        List<Record> indexes =getAllIndexes(dao.ar()).where("TABLE_NAME",meta.getName().toUpperCase()).find();
        Map<String, String> indexesMap = getIndexesMap(indexes);

        for (Record record : list) {
            String column = record.getString("column_name");
            ColumnMeta columnMeta = meta.getColumn(column);
            if (columnMeta == null) {
                // 没有？不可能
                log.warn("找不到对应的字段:" + column);
                continue;
            }
            fill(columnMeta,record,indexesMap);
        }

    }
    private String getRecordKey(Record record) {
        return new StringBuilder().append(record.getString("table_name"))
                .append(record.getString("column_name")).toString();
    }

    private Map<String,String> getIndexesMap(List<Record> consts ){
        Map<String, String> keyTypes = new HashMap<String, String>();
        for (Record record : consts) {
            String keyType = record.getString("index_type_name");
            keyTypes.put(getRecordKey(record), keyType);
        }
        return keyTypes;
    }
    @Override
    public Snapshot takeSnapshot() {
        List<TableNameAndComment> nameAndComments = getNameAndComments();
        List<Record> allColumns = getAllColumns(dao.ar()).find();
        List<Record> indexes =getAllIndexes(dao.ar()).find();
        final Map<String, String> indexesMap = getIndexesMap(indexes);
        List<ColumnMeta> columnMetas = CollectionUtils.map(allColumns, new Converter<Record, ColumnMeta>() {
            @Override
            public ColumnMeta convert(Record record) {
                ColumnMeta columnMeta = new ColumnMeta();
                String table = record.getString("table_name").toLowerCase();
                columnMeta.setTable(table);
                fill(columnMeta,record,indexesMap);
                return columnMeta;
            }
        });


        final Map<String,List<ColumnMeta>> treeMap = toTreeMap(columnMetas);

        ///tables
        List<TableMeta> tableMetas =  CollectionUtils.map(nameAndComments, new Converter<TableNameAndComment, TableMeta>() {
            @Override
            public TableMeta convert(TableNameAndComment data) {
                List<ColumnMeta> children = treeMap.get(data.getName().toLowerCase());
                if(children==null){
                    throw new DaoException("找不到对应的表"+data.getName());
                }
                TableMeta tableMeta = new TableMeta();
                tableMeta.setName(data.getName());
                tableMeta.setComment(data.getComment());
                //Comment
                tableMeta.setColumns(children.toArray(new ColumnMeta[children.size()]));
                return tableMeta;
            }
        });



        ZoomSnapshot snapshot= new ZoomSnapshot();
        snapshot.setTables(tableMetas);
        return snapshot;
    }


}
