package org.zoomdev.zoom.dao.alias.impl;

import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.alias.NameAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过两个map来确定改名规则
 *
 * @author jzoom
 */
public class MapNameAdapter implements NameAdapter {
    Map<String, String> field2columnMap;
    Map<String, String> column2fieldMap;


    public static NameAdapter fromEntity(Entity entity) {
        Map<String, String> field2columnMap = new HashMap<String, String>();
        Map<String, String> column2fieldMap = new HashMap<String, String>();
        for (EntityField field : entity.getEntityFields()) {
            field2columnMap.put(field.getFieldName(), field.getColumnName());
            column2fieldMap.put(field.getColumnName(), field.getFieldName());
        }


        return new MapNameAdapter(field2columnMap, column2fieldMap);

    }

    public MapNameAdapter(Map<String, String> field2columnMap,
                          Map<String, String> column2fieldMap) {
        super();
        this.field2columnMap = field2columnMap;
        this.column2fieldMap = column2fieldMap;
    }


    @Override
    public String getFieldName(String column) {
        String field = column2fieldMap.get(column);
        if (field == null) {
            return column;
        }
        return field;
    }


    @Override
    public String getColumnName(String field) {
        String column = field2columnMap.get(field);
        if (column == null) {
            return field;
        }
        return column;
    }


}
