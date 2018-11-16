package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.alias.NameAdapter;
import org.zoomdev.zoom.dao.alias.AliasPolicy;

import java.util.Map;

/**
 * 数据库字段名称=》实体类字段名称的映射通过 前缀来修改
 * 实体类形成=》 数据库字段名称 通过map来确认
 * 这个用于单表的改名
 *
 * @author jzoom
 */
public class PrefixMapNameAdapter implements NameAdapter {

    private AliasPolicy aliasPolicy;
    private Map<String, String> field2ColumnMap;

    public PrefixMapNameAdapter(AliasPolicy aliasPolicy, Map<String, String> field2ColumnMap) {
        this.aliasPolicy = aliasPolicy;
        this.field2ColumnMap = field2ColumnMap;
    }


    @Override
    public String getFieldName(String column) {
        return aliasPolicy.getAlias(column);
    }


    @Override
    public String getColumnName(String field) {
        String name = field2ColumnMap.get(field);
        if (name == null) {
            return field;
        }
        return name;
    }


}
