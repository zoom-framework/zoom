package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.filter.pattern.PatternFilter;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.adapters.NameAdapter;
import org.zoomdev.zoom.dao.meta.ColumnMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class BeanEntity extends AbstractEntity {

    Class<?> type;

    private Map<String,String> file2column;

    private NameAdapter nameAdapter;

    BeanEntity(String table,
               EntityField[] entityFields,
               EntityField[] primaryKeys,
               AutoEntity autoEntity,
               Class<?> type,
               NameAdapter nameAdapter) {
        super(table, entityFields, primaryKeys, autoEntity);
        this.type = type;
        this.file2column = new ConcurrentHashMap<String, String>();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Object newInstance() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new DaoException("初始化类失败"+type,e);
        }
    }

    @Override
    public String getColumnName(String field) {
        if(field==null){
            throw new NullPointerException("字段名称为空");
        }
        String column = file2column.get(field);
        if(column==null){
            for(EntityField entityField : entityFields){
                if(entityField.getFieldName().equals(field)){
                    column = entityField.getColumnName();
                    file2column.put(field,column);
                    break;
                }
            }
            if (column == null) {
                column = nameAdapter.getColumnName(field);
                if(column!=null){
                    file2column.put(field,column);
                }else{
                    throw new DaoException(
                        String.format("找不到字段%s对应的列名称,所有可能的字段列表为",
                            field));
                }
            }
        }
        return column;
    }


    private String getFieldsList(){

        return null;
    }
}
