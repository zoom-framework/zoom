package org.zoomdev.zoom.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.caster.ValueCaster;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.Sql;
import org.zoomdev.zoom.dao.alias.NameAdapter;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.utils.DaoUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;


public class BuilderKit {


    /**
     * 构建插入语句
     *
     * @param sql
     * @param values
     * @param driver
     * @param table
     * @param record
     */
    public static void buildInsert(StringBuilder sql, List<Object> values, SqlDriver driver, String table, Record record) {

        sql.append("INSERT INTO ").append(table).append(" (");
        boolean first = true;
        for (Entry<String, Object> entry : record.entrySet()) {
            Object value = entry.getValue();
            String name = entry.getKey();
            if (first) {
                first = false;
            } else {
                sql.append(COMMA);
            }
            values.add(value);
            driver.protectColumn(sql, name);
        }
        sql.append(") VALUES (");
        //?
        join(sql, record.size())
                .append(')');

    }

    public static final char QM = '?';  //Question Mark
    public static final char COMMA = ',';  //comma

    /**
     * 问号    组合成  ?,?
     *
     * @param sql
     * @param size 问号个数
     * @return
     */
    public static StringBuilder join(StringBuilder sql, int size) {
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                sql.append(COMMA);
            }
            sql.append(QM);
        }
        return sql;
    }

    private static final Log log = LogFactory.getLog(Sql.class);


    public static PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            List<Object> values,
            String[] generatedKeys,boolean output) throws SQLException {
        if(output){
            log.info(String.format(sql.replace("?", "'%s'"),
                    values.toArray(new Object[values.size()])));
        }


        PreparedStatement ps = connection.prepareStatement(sql, generatedKeys);
        for (int index = 1, c = values.size(); index <= c; ++index) {
            ps.setObject(index, values.get(index - 1));
        }
        return ps;
    }


    public static PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            List<Object> values,boolean output) throws SQLException {
        if(output){
            log.info(String.format(sql.replace("?", "'%s'"),
                    values.toArray(new Object[values.size()])));
        }


        PreparedStatement ps = connection.prepareStatement(sql);
        for (int index = 1, c = values.size(); index <= c; ++index) {
            ps.setObject(index, values.get(index - 1));
        }
        return ps;
    }


    private static ValueCaster blobCaster;
    private static ValueCaster clobCaster;

    static {

        blobCaster = Caster.wrap(Blob.class, byte[].class);
        clobCaster = Caster.wrap(Clob.class, String.class);

    }


    public static final Record buildOne(ResultSet rs, NameAdapter policy) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Record map = new Record();
        for (int i = 1; i <= columnCount; i++) {
            int type = rsmd.getColumnType(i);
            String name = rsmd.getColumnName(i);
            map.put(policy.getFieldName(name), getValue(type, rs, i));
        }

        return map;
    }

    private static Object getValue(int type, ResultSet rs, int i) throws SQLException {
        if (type < Types.BLOB)
            return rs.getObject(i);
        else if (type == Types.CLOB)
            return clobCaster.to(rs.getClob(i));
        else if (type == Types.NCLOB)
            return clobCaster.to(rs.getNClob(i));
        else if (type == Types.BLOB)
            return blobCaster.to(rs.getBlob(i));
        else
            return rs.getObject(i);
    }

    public static final Record build(int columnCount, ResultSet rs, int[] types, String[] labelNames) throws SQLException {
        Record map = new Record();
        for (int i = 1; i <= columnCount; i++) {
            map.put(labelNames[i], getValue(types[i], rs, i));
        }
        return map;
    }


    public static List<Record> executeQuery(Connection connection,
                                            SimpleSqlBuilder builder,
                                            NameAdapter nameAdapter,
                                            boolean output) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = BuilderKit.prepareStatement(connection, builder.sql.toString(), builder.values,output);
            rs = ps.executeQuery();
            return BuilderKit.build(rs, nameAdapter);
        } finally {
            DaoUtils.close(rs);
            DaoUtils.close(ps);
        }

    }

    public static final List<Record> build(ResultSet rs, NameAdapter policy) throws SQLException {
        List<Record> result = new ArrayList<Record>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] labelNames = new String[columnCount + 1];
        int[] types = new int[columnCount + 1];
        buildLabelNamesAndTypes(rsmd, labelNames, types, policy);
        while (rs.next()) {
            result.add(build(columnCount, rs, types, labelNames));
        }
        return result;
    }


    private static final void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types, NameAdapter policy) throws SQLException {
        for (int i = 1; i < labelNames.length; i++) {
            labelNames[i] = policy.getFieldName(rsmd.getColumnLabel(i));
            types[i] = rsmd.getColumnType(i);
        }
    }


    public static void buildUpdate(
            StringBuilder sql,
            List<Object> values,
            SqlDriver driver,
            String table,
            StringBuilder where,
            Record record
    ) {
        sql.append("UPDATE ").append(table);
        boolean first = true;
        int index = 0;
        for (Entry<String, Object> entry : record.entrySet()) {
            Object value = entry.getValue();
            if (first) {
                first = false;
                sql.append(" SET ");
            } else {
                sql.append(COMMA);
            }
            values.add(index++, value);
            driver.protectColumn(sql, entry.getKey()).append("=?");
        }

        if (where.length() > 0) {
            sql.append(" WHERE ").append(where);
        }

    }

    /**
     * 构建delete语句
     */
    public static void buildDelete(
            StringBuilder sql,
            String table,
            StringBuilder where
    ) {
        if (where.length() <= 0) {
            throw new DaoException("Whole table delete is not valid!");
        }
        sql.append("DELETE FROM ").append(table);
        if (where.length() > 0) {
            sql.append(" WHERE ").append(where);
        }
    }

    public static final Pattern AS_PATTERN = Pattern.compile("([a-z_\\(\\)\\.\\[\\]]+)[\\s]+as[\\s]+([a-z_]+)", Pattern.CASE_INSENSITIVE);


    public static Integer executeUpdate(Connection connection,
                                        SimpleSqlBuilder builder,boolean output) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = BuilderKit.prepareStatement(connection, builder.sql.toString(), builder.values,output);
            return ps.executeUpdate();
        } finally {
            DaoUtils.close(ps);
        }
    }

    public static Integer executeInsert(Connection connection,
                                        SimpleSqlBuilder builder,
                                        String[] generatedKeys,
                                        Map<String, Object> record,
                                        boolean output) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = BuilderKit.prepareStatement(connection, builder.sql.toString(), builder.values, generatedKeys,output);
            int ret = ps.executeUpdate();
            if (ret > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    for (int i = 0; i < generatedKeys.length; ++i) {
                        Object value = rs.getObject(i + 1);
                        record.put(generatedKeys[i], value);
                    }
                }
            }
            return ret;
        } finally {
            DaoUtils.close(rs);
            DaoUtils.close(ps);
        }
    }
}
