package org.zoomdev.zoom.dao.meta;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.zoomdev.zoom.dao.validator.*;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段属性
 *
 * @author jzoom
 */
public class ColumnMeta implements Serializable {


    public boolean isIndex() {
        return keyType == KeyType.INDEX;
    }

    public static enum KeyType {
        PRIMARY,
        UNIQUE,
        INDEX
    }

    /**
     * 数据库中的类型
     */
    private Class<?> dataType;

    private String table;

    /**
     * Sql中的type
     */
    private int type;


    @JsonIgnore
    private KeyType keyType;

    private boolean nullable;

    private int maxLen;




    public Validator[] getValidators() {
        return validators;
    }

    public void setValidators(Validator[] validators) {
        this.validators = validators;
    }

    @JsonIgnore
    private Validator[] validators;

    /**
     * 是否自动提交
     * 怎么来判断呢，需要使用driver来，
     * 某些情况下的数据库trigger会自动插入值，需要有一个factory来解释这种情况.
     * 比如oracle查询到对应的trigger则认为是自动增长.   driver.isAuto( table,column ), 要有这么个方法。
     * 比如有些值是在插入的时候使用函数更新，比如mysql的timestamp,这些都需要来判断
     */
    private boolean auto;

    /**
     * 长度
     */
    private int length;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 数据库中的原始类型，比如mysql的字符串: varchar(100) oracle的VARCHAR2(200)
     */
    private String rawType;

    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<?> type) {
        this.dataType = type;
    }

    public boolean isPrimary() {
        return keyType == KeyType.PRIMARY;
    }

    public boolean isAuto() {
        return auto;
    }

    public boolean isUnique() {
        return keyType == KeyType.UNIQUE;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawType() {
        return rawType;
    }

    public void setRawType(String rawType) {
        this.rawType = rawType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String comment;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }


    @Override
    public String toString() {
        return name+":"+comment;
    }
}
