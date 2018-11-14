package org.zoomdev.zoom.dao.driver.mysql;

import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.driver.AbsDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;
import org.zoomdev.zoom.dao.migrations.ZoomDatabaseBuilder;

import java.net.URI;
import java.net.URL;
import java.sql.Types;
import java.util.*;
import java.util.Map.Entry;

public class MysqlDriver extends AbsDriver {

    @Override
    public StringBuilder protectColumn(StringBuilder sb, String name) {
        int n;
        if ((n = name.indexOf(".")) > 0) {
            String table = name.substring(0, n);
            String column = name.substring(n + 1);
            return protectName(sb,table).append(".").append(column);
        }
        return protectName(sb,name);
    }

    @Override
    public String protectColumn(String name) {
        int n;
        if ((n = name.indexOf(".")) > 0) {
            String table = name.substring(0, n);
            String column = name.substring(n + 1);

            return protectName(new StringBuilder(),table)
                    .append(".").append(column).toString();
        }
        return protectName(name);
    }

    @Override
    public String protectTable(String tableName) {

        return protectColumn(tableName);
    }

    @Override
    public String getTableCatFromUrl(String url) {
        //jdbc:mysql://SERVER:PORT/DBNAME?useUnicode=true&characterEncoding=UTF-8
        int n = url.lastIndexOf("?");
        if(n > 0){
            url = url.substring(0,n);
        }

        return url.substring(url.lastIndexOf("/")+1);
    }

    protected String protectName(String name) {
        return protectName(new StringBuilder(), name).toString();
    }

    protected StringBuilder protectName(
            StringBuilder sb, String name
    ) {
        return sb
                .append('`').append(name).append("`");
    }

    @Override
    public StatementAdapter get(Class<?> dataClass, Class<?> columnClass) {
        return super.get(dataClass, columnClass);
    }

    @Override
    public StringBuilder buildLimit(
            StringBuilder sql,
            List<Object> values,
            int position, int size) {
        values.add(position);
        values.add(size);
        return sql.append(" LIMIT ?,?");

    }


    public void insertOrUpdate(StringBuilder sb, List<Object> values, String tableName, Map<String, Object> data, String... unikeys) {
        StringBuilder signs = new StringBuilder();
        sb.append("INSERT INTO ")
                .append(tableName)
                .append(" (");
        boolean first = true;
        for (Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (first) {
                first = false;
            } else {
                signs.append(',');
                sb.append(',');
            }
            sb.append(key);
            signs.append('?');
            values.add(value);
        }
        sb.append(") VALUES (").append(signs).append(") ON DUPLICATE KEY UPDATE ");
        first = true;
        Set<String> keySet = new HashSet<String>();
        for (String string : unikeys) {
            keySet.add(string);
        }
        for (Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            if (keySet.contains(key)) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(key).append("=?");
            values.add(entry.getValue());
        }
    }

    @Override
    public void build(TableBuildInfo table, List<String> sqlList) {

        List<String> primaryKeys = new ArrayList<String>(3);
        StringBuilder sb = new StringBuilder();

        for (ColumnMeta columnMeta : table.getColumns()) {
            if(columnMeta.isPrimary()){
                primaryKeys.add(columnMeta.getName());
            }
        }

        sb.append("CREATE TABLE ");
        if (table.isCreateWhenNotExists()) {
            sb.append("IF NOT EXISTS ");
        }
        protectTable(sb, table.getName());
        sb.append("(\n");
        boolean first = false;
        int index = 0;
        for (ColumnMeta columnMeta : table.getColumns()) {
            sb.append("\t");
            protectColumn(sb, columnMeta.getName());
            sb.append(' ');
            try{
                sb.append(formatColumnType(columnMeta));
            }catch (Exception e){
                throw new RuntimeException("不支持的类型"+columnMeta.getName());
            }



            if (columnMeta.getDefaultValue() != null) {
                if (columnMeta.getDefaultValue() instanceof String) {
                    sb.append(" DEFAULT '").append(columnMeta.getDefaultValue()).append("'");
                } else {
                    if(columnMeta.getDefaultValue() instanceof ZoomDatabaseBuilder.FunctionValue){
                        sb.append(" DEFAULT ").append(((ZoomDatabaseBuilder.FunctionValue)columnMeta.getDefaultValue()).getValue());
                    }else{
                        sb.append(" DEFAULT ").append(columnMeta.getDefaultValue());
                    }

                }
            }else{
                if(columnMeta.isPrimary()){
                    if(columnMeta.isAuto()){
                        sb.append(" PRIMARY KEY");
                        sb.append(" auto_increment".toUpperCase());
                    }else{
                        //single primary key
                        if(primaryKeys.size() == 1){
                            sb.append(" PRIMARY KEY");
                        }
                    }
                }else{
                    sb.append(columnMeta.isNullable()
                            ? " NULL"
                            : " NOT NULL");
                }
            }
            //sb.append(" COMMENT '").append(columnMeta.getComment()==null ? "":columnMeta.getComment()).append("'");

            if( index < table.getColumns().size() - 1){
                sb.append(",");
            }

            if(index == table.getColumns().size()-1){
                break;
            }
            sb.append("\n");
            ++index;
        }

        if(primaryKeys.size() > 1){
            first = true;

            sb.append(",\n\tPRIMARY KEY (");
            for(String key : primaryKeys){
                if(first){
                    first = false;
                }else{
                    sb.append(",");
                }
                sb.append(key);
            }
            sb.append(")\n");

        }else{
            sb.append("\n");
        }

        sb.append(")charset=utf8;\n");

        sqlList.add(sb.toString());

        //index

        for (ColumnMeta columnMeta : table.getColumns()) {

            if(columnMeta.isIndex()){
                sb.setLength(0);
                sb.append("CREATE INDEX ")
                        .append("IDX_")
                        .append(table.getName())
                        .append("_")
                        .append(columnMeta.getName())
                        .append(" ON ")
                        .append(table.getName())
                        .append("(")
                        .append(columnMeta.getName())
                        .append(");\n");
                sqlList.add(sb.toString());
            }
        }
    }

    @Override
    public int page2position(int page, int size) {
        return 0;
    }

    @Override
    public String formatColumnType(ColumnMeta columnMeta) {
        int type = columnMeta.getType();
        switch (type) {
            case Types.INTEGER:
                return "int(32)";
            case Types.SMALLINT:
                return "smallint(8)";
            case Types.BIGINT:
                return "bigint(64)";
            case Types.VARCHAR:
                return new StringBuilder().append("varchar(").append(columnMeta.getMaxLen()).append(")").toString();
            case Types.NVARCHAR:
                return new StringBuilder().append("varchar(").append(columnMeta.getMaxLen()).append(")").toString();
            case Types.DATE:
                return "date";
            case Types.TIME:
                return "time";
            case Types.BOOLEAN:
                return "tinyint(1)";
            case Types.TIMESTAMP:
                return "timestamp";
            case Types.CHAR:
                return new StringBuilder().append("char(").append(columnMeta.getMaxLen()).append(")").toString();
            case Types.CLOB:
                if (columnMeta.getMaxLen() == 65535) {
                    return new StringBuilder().append("text").toString();
                }
                return new StringBuilder().append("mediumtext").toString();
            case Types.NUMERIC:
                return "double";
            case Types.DOUBLE:
                return "double";
            case Types.BLOB:
                return "blob";
            default:
                throw new RuntimeException("不支持的类型" + columnMeta.getType());
        }

    }

    @Override
    public StringBuilder protectTable(StringBuilder sb, String name) {
        return protectColumn(sb,name);
    }




}
