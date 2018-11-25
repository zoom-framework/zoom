package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.caster.ValueCaster;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.dao.adapters.StatementAdapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 主要是处理Clob、Blob的适配问题，其他都可以由DataSource自己处理
 */
class StatementAdapters {

    static String2Clob STRING2CLOB = new String2Clob();
    static ByteArray2Blob BYTEARRAY2BLOB = new ByteArray2Blob();


    static Map<String, StatementAdapter> pool = new ConcurrentHashMap<String, StatementAdapter>();

    static String getKey(Class<?> fieldType, Class<?> columnType) {
        if(fieldType==null){
            return columnType.getName();
        }
        return new StringBuilder().append(fieldType.getName())+"2"+columnType.getName();
    }

    static StatementAdapter DEFAULT = new StatementAdapter() {
        @Override
        public void adapt(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value);
        }
    };

    static {


    }


    /**
     * 获取一个从实体类到数据库的数据适配器
     *
     * @param fieldType  实体类字段类型
     * @param columnType 数据库字段类型
     * @return
     */
    public static StatementAdapter create(Class<?> fieldType, Class<?> columnType) {
        if (fieldType == null) {
            if (columnType == null) {
                return DEFAULT;
            }
            return create(columnType);
        }
        if (columnType == null) {
            return DEFAULT;
        }
        if (fieldType == columnType || columnType.isAssignableFrom(fieldType)) {
            return DEFAULT;
        }

        String key = getKey(fieldType, columnType);
        StatementAdapter adapter = pool.get(key);
        if (adapter == null) {
            if (Clob.class.isAssignableFrom(columnType)) {
                if (fieldType == String.class) {
                    adapter= STRING2CLOB;
                }else{
                    adapter=new CasterProxyStatementAdapter(Caster.wrap(fieldType, String.class), STRING2CLOB);
                }
            } else if (Blob.class.isAssignableFrom(columnType)) {
                if (fieldType == byte[].class) {
                    adapter = BYTEARRAY2BLOB;
                }else{
                    adapter = new CasterProxyStatementAdapter(Caster.wrap(fieldType, byte[].class), BYTEARRAY2BLOB);
                }
            }else{
                adapter = new CasterProxyStatementAdapter(Caster.wrap(fieldType,columnType),DEFAULT);
            }
            pool.put(key,adapter);
        }

        return adapter;
    }

    public static StatementAdapter create(Class<?> columnType) {
        String key = getKey(null,columnType);
        StatementAdapter adapter = pool.get(key);
        if(adapter==null){
            if (Clob.class.isAssignableFrom(columnType)) {
                //先转成String
                adapter = new CasterProxyStatementAdapter(Caster.wrap(String.class),STRING2CLOB);
            } else if (Blob.class.isAssignableFrom(columnType)) {
                //先转成byte[]
                adapter = new CasterProxyStatementAdapter(Caster.wrap(byte[].class),BYTEARRAY2BLOB);
            }else{
                adapter = new CasterProxyStatementAdapter(Caster.wrap(columnType),DEFAULT);
            }
            pool.put(key,adapter);
        }

        return adapter;
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
