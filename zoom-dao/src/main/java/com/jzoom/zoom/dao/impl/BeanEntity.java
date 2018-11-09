package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.dao.DaoException;
import com.jzoom.zoom.dao.adapters.EntityField;

class BeanEntity extends AbstractEntity {

    Class<?> type;


    BeanEntity(String table,
               EntityField[] entityAdapters,
               EntityField[] primaryKeys,
               AutoEntity autoEntity,
               Class<?> type) {
        super(table, entityAdapters, primaryKeys, autoEntity);
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
