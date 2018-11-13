package org.zoomdev.zoom.dao.driver.oracle;

import org.zoomdev.zoom.dao.driver.AbsDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;

import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class OracleDriver extends AbsDriver {

    @Override
    public StringBuilder buildPage(StringBuilder sql, int position, int size) {
        return sql.insert(0, "SELECT * FROM(SELECT A.*, rownum r FROM (").append(") A WHERE rownum <= ")
                .append(position + size).append(" ) B WHERE r > ").append(position);
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
                throw new RuntimeException("不支持的类型"+struct.getDataType());
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

}
