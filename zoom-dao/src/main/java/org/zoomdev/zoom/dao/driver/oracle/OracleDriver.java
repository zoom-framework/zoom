package org.zoomdev.zoom.dao.driver.oracle;

import org.zoomdev.zoom.dao.driver.AbsDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;
import org.zoomdev.zoom.dao.migrations.ZoomDatabaseBuilder;

import java.sql.Types;
import java.util.*;
import java.util.Map.Entry;

public class OracleDriver extends AbsDriver {

    private OracleAutoIncreaseProvider autoIncreaseProvider;

    public OracleDriver(){
        this(new SimpleOracleAutoIncreaseProvider());
    }

    public OracleDriver(OracleAutoIncreaseProvider autoIncreaseProvider){
        this.autoIncreaseProvider = autoIncreaseProvider;
    }


    @Override
    public StringBuilder buildLimit(
            StringBuilder sql,
            List<Object> values,
            int position,
            int size) {
        values.add(position + size);
        values.add(position);
        return sql.insert(0,
                "SELECT * FROM (SELECT A.*, ROWNUM R FROM (")
                .append(") A WHERE ROWNUM <= ?) B WHERE R > ?");
    }
    public void setAutoIncreaseProvider(OracleAutoIncreaseProvider autoIncreaseProvider) {
        this.autoIncreaseProvider = autoIncreaseProvider;
    }

    @Override
    public int position2page(int position, int size) {
        ++position;
        if (position % size == 0) {
            return position / size;
        }
        return position / size + 1;
    }

    /**
     * 系统的position从0开始,page从1开始
     */
    @Override
    public int page2position(int page, int size) {

        return 0;
    }

    @Override
    public String formatColumnType(ColumnMeta struct) {
        switch (struct.getType()) {
            case Types.INTEGER:
                return "NUMBER";
            case Types.SMALLINT:
                return "NUMBER";
            case Types.BIGINT:
                return "NUMBER";
            case Types.VARCHAR:
                return new StringBuilder().append("VARCHAR2(")
                        .append(struct.getMaxLen()).append(")").toString();
            case Types.NVARCHAR:
                return new StringBuilder().append("NVARCHAR2(")
                        .append(struct.getMaxLen()).append(")").toString();
            case Types.DATE:
                return "date";
            case Types.TIME:
                return "time";
            case Types.BOOLEAN:
                return "NUMBER";
            case Types.TIMESTAMP:
                return "timestamp";
            case Types.CHAR:
                return new StringBuilder().append("CHAR(")
                        .append(struct.getMaxLen()).append(")").toString();
            case Types.CLOB:
                return "CLOB";
            case Types.BLOB:
                return "BLOB";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.NUMERIC:
                return "NUMBER";
            default:
                throw new RuntimeException("不支持的类型" + struct.getDataType());
        }
    }


    @Override
    public void insertOrUpdate(StringBuilder sb, List<Object> values, String tableName, Map<String, Object> data, String... unikeys) {

        sb.append("MERGE INTO").append(' ').append(tableName).append(" T1 ")
                .append("USING (SELECT ");


        boolean first = true;
        for (Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();


            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append("? AS ").append(key);
            values.add(entry.getValue());
        }

        Set<String> keys = new HashSet<String>();
        sb.append(" FROM DUAL) T2 ON ( ");

        first = true;
        for (String key : unikeys) {

            if (first) {
                first = false;
            } else {
                sb.append(" AND ");
            }

            sb.append("T1.").append(key).append("=").append("T2.").append(key);
            keys.add(key);
        }


        sb.append(") ").append("WHEN MATCHED THEN UPDATE SET ");


        first = true;
        for (Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            if (keys.contains(key)) {
                continue;
            }

            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append("T1.").append(key).append("=?");
            values.add(entry.getValue());
        }


        sb.append(" WHEN NOT MATCHED THEN INSERT (");

        first = true;
        for (Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();

            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append("T1.").append(key);
            values.add(entry.getValue());
        }

        sb.append(") VALUES (");

        first = true;
        for (int i = 0, c = data.size(); i < c; ++i) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("?");
        }

        sb.append(")");
    }

    @Override
    public String buildDropIfExists(String table) {
        String str = "declare num number; begin " +
                "select count(1) into num from user_tables where table_name = upper('"+table+"') ;" +
                "if num > 0 then " +
                "execute immediate 'drop table "+table+"' ;" +
                "end if;" +
                "end;";
        return str;
    }

    @Override
    public void buildTable(TableBuildInfo table, List<String> sqlList) {
        List<String> primaryKeys = new ArrayList<String>(3);

        ColumnMeta autoIncreaseColumn = null;

        StringBuilder sb = new StringBuilder();
        for (ColumnMeta columnMeta : table.getColumns()) {
            if (columnMeta.isPrimary()) {
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
            try {
                sb.append(formatColumnType(columnMeta));
            } catch (Exception e) {
                throw new RuntimeException("不支持的类型" + columnMeta.getName());
            }


            if (columnMeta.getDefaultValue() != null) {
                if (columnMeta.getDefaultValue() instanceof String) {
                    sb.append(" DEFAULT '").append(columnMeta.getDefaultValue()).append("'");
                } else {
                    if (columnMeta.getDefaultValue() instanceof ZoomDatabaseBuilder.FunctionValue) {
                        sb.append(" DEFAULT ").append(((ZoomDatabaseBuilder.FunctionValue) columnMeta.getDefaultValue()).getValue());
                    } else {
                        sb.append(" DEFAULT ").append(columnMeta.getDefaultValue());
                    }

                }
            } else {
                if (columnMeta.isPrimary()) {
                    if (columnMeta.isAuto()) {
                        sb.append(" PRIMARY KEY");
                        // sb.append(" auto_increment".toUpperCase());
                        autoIncreaseColumn = columnMeta;

                    } else {
                        //single primary key
                        if (primaryKeys.size() == 1) {
                            sb.append(" PRIMARY KEY");
                        }
                    }
                } else {
                    sb.append(columnMeta.isNullable() ? " NULL" : " NOT NULL");
                }
            }
            //sb.append(" COMMENT '").append(columnMeta.getComment()==null ? "":columnMeta.getComment()).append("'");

            if (index < table.getColumns().size() - 1) {
                sb.append(",");
            }

            if (index == table.getColumns().size() - 1) {
                break;
            }
            sb.append("\n");
            ++index;
        }

        if (primaryKeys.size() > 1) {
            first = true;

            sb.append(",\n\tPRIMARY KEY (");
            for (String key : primaryKeys) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(key);
            }
            sb.append(")\n");

        } else {
            sb.append("\n");
        }

        sb.append(")");
        sqlList.add(sb.toString());


        buildIndex(table,sqlList);

        if(autoIncreaseColumn!=null){
            if(autoIncreaseProvider!=null){
                autoIncreaseProvider.buildAutoIncrease(
                        table,autoIncreaseColumn,sqlList
                );
            }
        }



    }



    @Override
    public String protectTable(String tableName) {
        return tableName;
    }


    @Override
    public String getTableCatFromUrl(String url) {
        //jdbc:oracle:thin:@SERVER:PORT:DB
        return url.substring(url.lastIndexOf(":") + 1).toUpperCase();
    }


}
