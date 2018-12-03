package org.zoomdev.zoom.dao.utils;

import java.sql.*;

public class DaoUtils {
    public static final String SELECT_COUNT = "COUNT(*) AS COUNT_";

    public static void close(ResultSet rs) {
        if (rs != null) try {
            rs.close();
        } catch (Exception e) {
        }
    }

    public static void close(Statement ps) {

        if (ps != null) try {
            ps.close();
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
     *
     * @param dataType
     * @return
     */
    public static Class<?> normalizeType(Class<?> dataType) {
        if (Clob.class.isAssignableFrom(dataType)) {
            return String.class;
        }
        if (Blob.class.isAssignableFrom(dataType)) {
            return byte[].class;
        }
        return dataType;
    }

    public static int position2page(int position, int size) {
        if (position % size == 0) {
            return position / size + 1;
        }
        return position / size + 2;
    }
}
