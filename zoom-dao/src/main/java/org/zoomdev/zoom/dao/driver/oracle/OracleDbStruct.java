package org.zoomdev.zoom.dao.driver.oracle;

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

public class OracleDbStruct extends AbsDbStruct implements DbStructFactory {

    private String user;


    public OracleDbStruct(Dao dao, String user) {
        super(dao);
    }


    private static final Log log = LogFactory.getLog(OracleDbStruct.class);

    @Override
    public List<String> getTableNames() {
        List<Record> list = dao.ar()
                .nameAdapter(EmptyNameAdapter.DEFAULT)
                .executeQuery("SELECT TABLE_NAME FROM user_tab_comments");
        List<String> result = new ArrayList<String>();
        for (Record record : list) {
            result.add(record.getString("TABLE_NAME"));
        }
        return result;
    }


    private String getRecordKey(Record record) {
        return new StringBuilder().append(record.getString("TABLE_NAME"))
                .append(record.getString("COLUMN_NAME")).toString();
    }


    private Ar getAllKeyTypes(Ar ar){
        return ar.nameAdapter(EmptyNameAdapter.DEFAULT).table("user_cons_columns")
                .select("user_cons_columns.constraint_name,user_cons_columns.table_name, user_cons_columns.column_name,user_constraints.constraint_type")
                .join("user_constraints","user_cons_columns.constraint_name = user_constraints.constraint_name")
                .join("user_tables", " user_tables.table_name=user_cons_columns.table_name");
    }

    protected Map<String, String> getKeyTypes(String table) {
        List<Record> consts = getAllKeyTypes(dao.ar()).where("user_cons_columns.table_name",table.toUpperCase()).find();
        return getKeyTypesMap(consts);
    }


    private Map<String,String> getKeyTypesMap(List<Record> consts ){
        Map<String, String> keyTypes = new HashMap<String, String>();
        for (Record record : consts) {
            String keyType = record.getString("CONSTRAINT_TYPE");
            keyTypes.put(getRecordKey(record), keyType);
        }
        return keyTypes;
    }

    private Ar getAllIndexes(Ar ar){
        return ar.nameAdapter(EmptyNameAdapter.DEFAULT).table("user_ind_columns")
                .select("user_ind_columns.table_name,user_ind_columns.column_name,user_indexes.index_type")
                .join("user_indexes","user_ind_columns.index_name = user_indexes.index_name");
    }

    private Set<String> getAllIndexes(List<Record> indexs){
        Set<String> indexes = new HashSet<String>();
        for (Record record : indexs) {
            indexes.add(getRecordKey(record));
        }
        return indexes;
    }

    private Set<String> getIndexes(String tableName)
    {
        List<Record> indexes = getAllIndexes(dao.ar())
                .where("user_ind_columns.table_name",tableName.toUpperCase()).find();
        return getAllIndexes(indexes);
    }

    private String getRecordKey(String table, ColumnMeta columnMeta) {
        return new StringBuilder()
                .append(table.toUpperCase()).append(columnMeta.getName()).toString();
    }

    protected void fill(String table, ColumnMeta columnMeta, Record record, Map<String, String> keyTypes,Set<String> indexes) {
        columnMeta.setName(record.getString("COLUMN_NAME"));
        columnMeta.setComment(record.getString("COMMENTS"));

        String keyType = keyTypes.get(getRecordKey(table, columnMeta));
        if (keyType != null) {
            if (keyType.equals("P")) {
                columnMeta.setKeyType(KeyType.PRIMARY);
                //auto 另外计算
            } else if (keyType.equals("U")) {
                columnMeta.setKeyType(KeyType.UNIQUE);
            }else if(indexes.contains(getRecordKey(table, columnMeta))){
                columnMeta.setKeyType(KeyType.INDEX);
            }
        }



        columnMeta.setDefaultValue(record.getString("DATA_DEFAULT"));
        columnMeta.setNullable(record.getString("NULLABLE").equals("Y"));
        columnMeta.setMaxLen(record.getInt("DATA_LENGTH"));
        columnMeta.setRawType(record.getString("DATA_TYPE"));

    }


    protected Ar getAllColumns(Ar ar) {
        return ar.table("cols").nameAdapter(EmptyNameAdapter.DEFAULT)
                .select("cols.table_name,cols.column_name,cols.DATA_PRECISION,cols.NULLABLE,cols.DATA_DEFAULT,cols.DATA_TYPE,cols.DATA_LENGTH,COMMENTS")
                .join("user_tables", " user_tables.table_name=cols.table_name")
                .join("user_col_comments", "cols.COLUMN_NAME=user_col_comments.column_name and cols.TABLE_name=user_col_comments.TABLE_name","left");
    }


    private void fillWithAuto(TableMeta tableMeta) {
        for (ColumnMeta columnMeta : tableMeta.getColumns()) {
            OracleDriver oracleDriver = (OracleDriver) dao.getDriver();
            columnMeta.setAuto(oracleDriver.isAuto(dao, tableMeta, columnMeta));
        }

    }

    @Override
    public void fill(TableMeta meta) {
        List<Record> list = dao.ar()
                .nameAdapter(EmptyNameAdapter.DEFAULT)
                .executeQuery(
                        "SELECT TABLE_NAME as \"name\",COMMENTS as \"comment\" FROM user_tab_comments WHERE TABLE_NAME=?", meta.getName().toUpperCase());
        if (list.size() > 0) {
            meta.setComment(list.get(0).getString("comment"));
        } else {
            meta.setComment("");
        }

        Set<String> indexTypes = getIndexes(meta.getName().toUpperCase());
        Map<String, String> keyTypes = getKeyTypes(meta.getName().toUpperCase());

        List<Record> columns = getAllColumns(dao.ar()).where("cols.table_name", meta.getName().toUpperCase()).find();
//        List<Record> columns = dao.ar()
//                .nameAdapter(EmptyNameAdapter.DEFAULT)
//                .executeQuery("SELECT cols.table_name,cols.column_name,cols.DATA_PRECISION,cols.NULLABLE,cols.DATA_DEFAULT,cols.DATA_TYPE,cols.DATA_LENGTH,COMMENTS FROM cols "
//                        + "join user_tables on user_tables.table_name=cols.table_name "
//                        + "left join user_col_comments on cols.COLUMN_NAME=user_col_comments.column_name and cols.TABLE_name=user_col_comments.TABLE_name"
//                        + " where cols.table_name=?", meta.getName().toUpperCase());
        for (Record record : columns) {
            String column = record.getString("COLUMN_NAME");
            ColumnMeta columnMeta = meta.getColumn(column);
            if (columnMeta == null) {
                // 没有？不可能
                log.warn("找不到对应的字段:" + column);
                continue;
            }

            fill(meta.getName(), columnMeta, record, keyTypes,indexTypes);
        }

        fillWithAuto(meta);


    }

    @Override
    public List<TableNameAndComment> getNameAndComments()

    {
        List<Record> list = dao.ar()
                .executeQuery("SELECT TABLE_NAME as \"name\",COMMENTS as \"comment\" FROM user_tab_comments");

        List<TableNameAndComment> result = new ArrayList<TableNameAndComment>(list.size());
        for (Record record : list) {
            TableNameAndComment data = new TableNameAndComment();
            data.setName(record.getString("name").toLowerCase());
            data.setComment(record.getString("comment"));
            result.add(data);
        }

        return result;
    }

    @Override
    public Map<String, Collection<String>> getTriggers() {
        List<Record> triggers = dao.ar()
                .table("all_triggers")
                .nameAdapter(EmptyNameAdapter.DEFAULT)
                .join("user_users", "all_triggers.owner=user_users.username")
                .select("table_name,trigger_name")
                //.where("owner", user.toUpperCase())
                .find();
        Map<String, Collection<String>> triggerMap = new HashMap<String, Collection<String>>();
        for (Record record : triggers) {
            String tableName = record.getString("TABLE_NAME");
            Collection<String> list = triggerMap.get(tableName);
            if (list == null) {
                list = new ArrayList<String>();
                triggerMap.put(tableName, list);
            }
            list.add(record.getString("TRIGGER_NAME"));
        }
        return triggerMap;
    }


    @Override
    public Collection<String> getSequences() {
        List<Record> allSequnce = dao.ar()
                .table("all_sequences")
                .nameAdapter(EmptyNameAdapter.DEFAULT)
                .join("user_users", "all_sequences.SEQUENCE_OWNER=user_users.username")
                .select("SEQUENCE_NAME")
                // .where("SEQUENCE_OWNER", user.toUpperCase())
                .find();

        Set<String> sequences = new HashSet<String>();
        for (Record record : allSequnce) {
            sequences.add(record.getString("SEQUENCE_NAME"));
        }
        return sequences;
    }





    @Override
    public Snapshot takeSnapshot() {

        List<TableNameAndComment> nameAndComments = getNameAndComments();
        List<Record> allColumns = getAllColumns(dao.ar()).find();
        final Map<String,String> keyTypes =getKeyTypesMap(getAllKeyTypes(dao.ar()).find()) ;
        final Set<String> indexes = getAllIndexes(getAllIndexes(dao.ar()).find());
        List<ColumnMeta> columnMetas = CollectionUtils.map(allColumns, new Converter<Record, ColumnMeta>() {
            @Override
            public ColumnMeta convert(Record record) {
                ColumnMeta columnMeta = new ColumnMeta();
                String table = record.getString("TABLE_NAME");
                columnMeta.setTable(table.toLowerCase());
                fill(table,columnMeta,record,keyTypes,indexes);
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

                fillWithAuto(tableMeta);
                return tableMeta;
            }
        });



        ZoomSnapshot snapshot= new ZoomSnapshot();
        snapshot.setTables(tableMetas);


        return snapshot;
    }
}
