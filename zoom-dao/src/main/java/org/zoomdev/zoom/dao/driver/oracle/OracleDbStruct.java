package org.zoomdev.zoom.dao.driver.oracle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.alias.impl.EmptyNameAdapter;
import org.zoomdev.zoom.dao.alias.impl.ToLowerCaseNameAdapter;
import org.zoomdev.zoom.dao.driver.AbsDbStruct;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.ColumnMeta.KeyType;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.*;

public class OracleDbStruct extends AbsDbStruct implements DbStructFactory {

    private String user;


    public OracleDbStruct(Dao dao, String user) {
        super(dao);
//        try{
//            List<Record> list = dao.ar().executeQuery("select USERNAME from user_users");
//            if(list.size() > 0){
//                Record record = list.get(0);
//                user = record.getString("USERNAME");
//            }
//        }catch (Exception e){
//            throw new DaoException("查询用户信息表user_users失败，请确认本账户是否可以查看用户信息",e);
//        }

        //System.out.println(list);
    }



    private static final Log log = LogFactory.getLog(OracleDbStruct.class);

    @Override
    public Collection<String> getTableNames() {
        List<Record> list = dao.ar().executeQuery("SELECT TABLE_NAME FROM user_tab_comments");
        List<String> result = new ArrayList<String>();
        for (Record record : list) {
            result.add(record.getString("TABLE_NAME"));
        }
        return result;
    }

    protected Map<String, String> getKeyTypes(String table) {
        List<Record> consts = dao.ar()
                .executeQuery("select user_cons_columns.constraint_name,user_cons_columns.table_name, user_cons_columns.column_name,user_constraints.constraint_type from user_cons_columns "
                        + "join user_constraints on user_cons_columns.constraint_name = user_constraints.constraint_name "
                        + "where user_cons_columns.table_name=?", table);
        Map<String, String> keyTypes = new HashMap<String, String>();
        for (Record record : consts) {

            String key = record.getString("CONSTRAINT_TYPE");
            keyTypes.put(new StringBuilder().append(record.getString("TABLE_NAME"))
                    .append(record.getString("COLUMN_NAME")).toString(), key);
        }
        return keyTypes;
    }

    Map<String, String> getIndexType(String tableName)

    {
        List<Record> indexs = dao.ar().executeQuery("select user_ind_columns.table_name,user_ind_columns.column_name,user_indexes.index_type from user_ind_columns "
                + "join user_indexes on user_ind_columns.index_name = user_indexes.index_name "
                + "where user_ind_columns.table_name=?", tableName);
        Map<String, String> indexTypes = new HashMap<String, String>(indexs.size());
        for (Record record : indexs) {
            indexTypes.put(new StringBuilder().append(record.getString("TABLE_NAME"))
                    .append(record.getString("COLUMN_NAME")).toString(), "I");
        }
        return indexTypes;
    }

    @Override
    public void fill(TableMeta meta) {
        List<Record> list = dao.ar()
                .executeQuery(
                        "SELECT TABLE_NAME as \"name\",COMMENTS as \"comment\" FROM user_tab_comments WHERE TABLE_NAME=?", meta.getName().toUpperCase());
        if (list.size() > 0) {
            meta.setComment(list.get(0).getString("comment"));
        } else {
            meta.setComment("");
        }

        Map<String, String> keyTypes = getKeyTypes(meta.getName().toUpperCase());

        List<Record> columns = dao.ar()
                .executeQuery("SELECT cols.table_name,cols.column_name,cols.DATA_PRECISION,cols.NULLABLE,cols.DATA_DEFAULT,cols.DATA_TYPE,cols.DATA_LENGTH,COMMENTS FROM cols "
                + "join user_tables on user_tables.table_name=cols.table_name "
                + "left join user_col_comments on cols.COLUMN_NAME=user_col_comments.column_name and cols.TABLE_name=user_col_comments.TABLE_name"
                + " where cols.table_name=?", meta.getName().toUpperCase());
        for (Record record : columns) {
            String column = record.getString("COLUMN_NAME");
            ColumnMeta columnMeta = meta.getColumn(column);
            if (columnMeta == null) {
                // 没有？不可能
                log.warn("找不到对应的字段:" + column);
                continue;
            }

            columnMeta.setComment(record.getString("COMMENTS"));

            //key type
            //columnMeta.setAuto(record.getString("EXTRA").equals("auto_increment"));

            String keyType = keyTypes.get(new StringBuilder()
                    .append(meta.getName().toUpperCase()).append(column).toString());
            if (keyType != null) {
                if (keyType.equals("P")) {
                    columnMeta.setKeyType(KeyType.PRIMARY);
                    //auto 另外计算
                } else if (keyType.equals("U")) {
                    columnMeta.setKeyType(KeyType.UNIQUE);
                } else if (keyType.equals("I")) {
                    columnMeta.setKeyType(KeyType.INDEX);
                }
            }

            columnMeta.setDefaultValue(record.getString("DATA_DEFAULT"));
            columnMeta.setNullable(record.getString("NULLABLE").equals("Y"));
            columnMeta.setMaxLen(record.getInt("DATA_LENGTH"));
            columnMeta.setRawType(record.getString("DATA_TYPE"));

        }



    }

    @Override
    public Collection<TableNameAndComment> getNameAndComments()

    {
        List<Record> list = dao.ar()
                .executeQuery("SELECT TABLE_NAME as \"name\",COMMENTS as \"comment\" FROM user_tab_comments");

        List<TableNameAndComment> result = new ArrayList<TableNameAndComment>(list.size());
        for (Record record : list) {
            TableNameAndComment data = Caster.to(record, TableNameAndComment.class);
            data.setName(StringUtils.lowerCase(data.getName()));
            result.add(data);
        }

        return result;
    }

    @Override
    public Map<String, Collection<String>> getTriggers() {
       List<Record> triggers = dao.ar()
               .table("all_triggers")
               .nameAdapter(EmptyNameAdapter.DEFAULT)
               .join("user_users","all_triggers.owner=user_users.username")
                .select("table_name,trigger_name")
                //.where("owner", user.toUpperCase())
               .find();
        Map<String, Collection<String>> triggerMap = new HashMap<String, Collection<String>>();
        for (Record record : triggers) {
            String tableName = record.getString("TABLE_NAME");
            Collection<String> list = triggerMap.get(tableName);
            if(list==null){
                list = new ArrayList<String>();
                triggerMap.put(tableName,list);
            }
            list.add( record.getString("TRIGGER_NAME"));
        }
        return triggerMap;
    }


    @Override
    public Collection<String> getSequences() {
        List<Record> allSequnce = dao.ar()
                .table("all_sequences")
                .nameAdapter(EmptyNameAdapter.DEFAULT)
                .join("user_users","all_sequences.SEQUENCE_OWNER=user_users.username")
                .select("SEQUENCE_NAME")
               // .where("SEQUENCE_OWNER", user.toUpperCase())
                .find();

        Set<String> sequences = new HashSet<String>();
        for (Record record : allSequnce) {
            sequences.add(record.getString("SEQUENCE_NAME"));
        }
        return sequences;
    }

}
