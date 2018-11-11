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


    BeanEntity(String table,
               EntityField[] entityFields,
               EntityField[] primaryKeys,
               AutoEntity autoEntity,
               Class<?> type,
               NameAdapter nameAdapter) {
        super(table, entityFields, primaryKeys, autoEntity,nameAdapter);
        this.type = type;

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


}
