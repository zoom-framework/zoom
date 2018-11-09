package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.caster.ValueCaster;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class StatementAdapters {

    static String2Clob STRING2CLOB = new String2Clob();
    static ByteArray2Blob BYTEARRAY2BLOB = new ByteArray2Blob();

    static StatementAdapter DEFAULT = new StatementAdapter() {
        @Override
        public void adapt(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index,value);
        }
    };

    /**
     * 获取一个从实体类到数据库的数据适配器
     *
     * @param fieldType  实体类字段类型
     * @param columnType 数据库字段类型
     * @return
     */
    public static StatementAdapter create(Class<?> fieldType, Class<?> columnType) {
        if (fieldType == columnType || columnType.isAssignableFrom(fieldType)) {
            return DEFAULT;
        }
        if (columnType == Clob.class) {
            if (fieldType == String.class) {
                return STRING2CLOB;
            }
            //先转成String
            return new CasterProxyStatementAdapter(Caster.wrap(fieldType, String.class), STRING2CLOB);
        } else if (columnType == Blob.class) {
            if (fieldType == byte[].class) {
                return BYTEARRAY2BLOB;
            }
            //先转成byte[]
            return new CasterProxyStatementAdapter(Caster.wrap(fieldType, byte[].class), BYTEARRAY2BLOB);
        }
        return DEFAULT;
        //return new CasterStatementAdapter(Caster.wrap(fieldType, columnType));
    }

    public static StatementAdapter create(Class<?> columnType) {
        if (columnType == Clob.class) {
            //先转成String
            return new CasterProxyStatementAdapter(Caster.wrap(String.class), STRING2CLOB);
        } else if (columnType == Blob.class) {
            //先转成byte[]
            return new CasterProxyStatementAdapter(Caster.wrap(byte[].class), BYTEARRAY2BLOB);
        }
        return DEFAULT;
    }

    static class ByteArray2Blob implements StatementAdapter {

        @Override
        public void adapt(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value == null) {
                statement.setObject(index, null);
            } else {
                byte[] bytes = (byte[]) value;
                ByteArrayInputStream inputStream = null;
                try {
                    inputStream = new ByteArrayInputStream(bytes);
                    statement.setBinaryStream(index, inputStream, bytes.length);
                } finally {
                    Io.close(inputStream);
                }
            }
        }
    }

    static class String2Clob implements StatementAdapter {

        @Override
        public void adapt(PreparedStatement statement, int index, Object value) throws SQLException {
            if (value == null) {
                statement.setObject(index, null);
            } else {
                String string = (String) value;
                statement.setCharacterStream(index, new StringReader(string), string.length());
            }
        }
    }


    /**
     * 先转下格式，然后在调用setObject
     * Map/Set等集合形式需要先转成json格式才行
     */
    static class CasterStatementAdapter implements StatementAdapter {

        private ValueCaster valueCaster;

        public CasterStatementAdapter(ValueCaster valueCaster) {
            this.valueCaster = valueCaster;
        }

        @Override
        public void adapt(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, valueCaster.to(value));
        }
    }

    /**
     * 比如Map到clob
     */
    static class CasterProxyStatementAdapter implements StatementAdapter {
        private ValueCaster valueCaster;
        private StatementAdapter statementAdapter;

        public CasterProxyStatementAdapter(ValueCaster valueCaster, StatementAdapter statementAdapter) {
            this.valueCaster = valueCaster;
            this.statementAdapter = statementAdapter;
        }

        @Override
        public void adapt(PreparedStatement statement, int index, Object value) throws SQLException {
            value = valueCaster.to(value);
            statementAdapter.adapt(statement, index, value);
        }
    }


}
