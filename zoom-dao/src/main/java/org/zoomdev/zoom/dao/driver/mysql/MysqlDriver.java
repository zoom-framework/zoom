package org.zoomdev.zoom.dao.driver.mysql;

import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.driver.AbsDriver;
import org.zoomdev.zoom.dao.meta.ColumnMeta;

import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MysqlDriver extends AbsDriver {

    @Override
    public StringBuilder protectColumn(StringBuilder sb, String name) {
        if (name.contains(".")) {
            sb.append(name);
            return sb;
        }
        return sb.append('`').append(name).append('`');
    }

    @Override
    public String protectColumn(String name) {
        int n;
        if ((n = name.indexOf(".")) > 0) {
            String table = name.substring(0, n);
            String column = name.substring(n + 1);
            return new StringBuilder().append('`').append(table).append("`.`").append(column).append('`').toString();
        }
        return new StringBuilder().append('`').append(name).append('`').toString();
    }

    @Override
    public StatementAdapter get(Class<?> dataClass, Class<?> columnClass) {
        return super.get(dataClass, columnClass);
    }

    @Override
    public StringBuilder buildPage(StringBuilder sql, int position, int size) {
        return sql.append(" LIMIT ").append(position).append(',').append(size);
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
                return "int(32)";
            case Types.DOUBLE:
                return "double";
            case Types.BLOB:
                return "blob";
            default:
                throw new RuntimeException("不支持的类型" + columnMeta.getDataType());
        }

    }


}
