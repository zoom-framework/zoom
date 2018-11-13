package org.zoomdev.zoom.dao.utils;

import org.zoomdev.zoom.dao.Record;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaoUtils {
    public static void close(ResultSet rs) {
        if (rs != null) try {
            rs.close();
        } catch (Exception e) {
        }
    }

    public static void close(PreparedStatement ps) {

        if (ps != null) try {
            ps.close();
        } catch (Exception e) {
        }
    }

    public static void close(Connection connection) {
        if (connection != null) try {
            connection.close();
        } catch (Exception e) {
        }
    }


    /**
     * 将list转为map
     *
     * @param list
     * @param pk
     * @return
     */
    public static Map<Object, Record> list2map(List<Record> list, String pk) {
        if (list == null) return null;
        Map<Object, Record> map = new HashMap<Object, Record>(list.size());
        for (Record record : list) {
            map.put(record.get(pk), record);
        }
        return map;
    }

    /**
     * 判断一个数据库中的字段类型是否是Stream类型，Clob/Blob
     *
     * @param dataType
     * @return
     */
    public static boolean isStream(Class<?> dataType) {
        return Clob.class.isAssignableFrom(dataType) || Blob.class.isAssignableFrom(dataType);
    }

    /**
     * 如果是Stream，获取对应的类型
     * @param dataType
     * @return
     */
    public static Class<?> normalizeType(Class<?> dataType) {
        if(Clob.class.isAssignableFrom(dataType)){
            return String.class;
        }
        if(Blob.class.isAssignableFrom(dataType)){
            return byte[].class;
        }
        return dataType;
    }
}
