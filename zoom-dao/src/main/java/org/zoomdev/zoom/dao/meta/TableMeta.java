package org.zoomdev.zoom.dao.meta;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.zoomdev.zoom.common.json.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表属性
 *
 * @author jzoom
 */
public class TableMeta {

    /**
     * 如果有comment，表示已经被填充，下次就不用再次fill了
     */
    private String comment;

    /**
     * 表名称
     */
    private String name;

    /**
     * 字段meta
     */
    private ColumnMeta[] columns;

    /**
     * 保存原始名称 + ColumnMeta的映射
     */
    @JsonIgnore
    private Map<String, ColumnMeta> columnMap;

    public ColumnMeta[] getColumns() {
        return columns;
    }

    /**
     * 根据字段原始名称查找字段
     *
     * @param name
     * @return
     */
    public ColumnMeta getColumn(String name) {
        return columnMap.get(name);
    }

    public void setColumns(ColumnMeta[] columns) {
        columnMap = new HashMap<String, ColumnMeta>();
        this.columns = columns;
        for (ColumnMeta columnMeta : columns) {
            columnMap.put(columnMeta.getName(), columnMeta);
        }
    }

    @Override
    public String toString() {
        return JSON.stringify(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 避免在序列化json的时候使用这个，因为已经包含了主键信息，这里再来就画蛇添足了。
     */
    @JsonIgnore
    private ColumnMeta[] primaryKeys;


    @JsonIgnore
    public ColumnMeta[] getPrimaryKeys() {
        ColumnMeta[] primaryKeys = this.primaryKeys;
        if (primaryKeys == null) {
            List<ColumnMeta> list = new ArrayList<ColumnMeta>(3);
            for (ColumnMeta columnMeta : columns) {
                if (columnMeta.isPrimary()) {
                    list.add(columnMeta);
                }
            }
            primaryKeys = list.toArray(new ColumnMeta[list.size()]);
            this.primaryKeys = primaryKeys;
        }
        return primaryKeys;
    }


}
