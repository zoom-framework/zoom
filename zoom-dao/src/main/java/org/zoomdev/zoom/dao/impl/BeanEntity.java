package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.meta.JoinMeta;

import java.util.Map;

class BeanEntity extends AbstractEntity {

    Class<?> type;

    private JoinMeta[] joins;


    BeanEntity(String table,
               EntityField[] entityFields,
               EntityField[] primaryKeys,
               AutoEntity autoEntity,
               Class<?> type,
               Map<String,String> namesMap,
               JoinMeta[] joins) {
        super(table, entityFields, primaryKeys, autoEntity,namesMap);
        if (primaryKeys.length == 0) {
            throw new DaoException("绑定实体类" + type + "至少需要定义一个主键");
        }
        this.joins = joins;
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

    @Override
    public void setQuerySource(SqlBuilder builder) {
        builder.table(table);
        if(joins!=null){
            for(JoinMeta joinMeta : joins){
                builder.join(joinMeta.getTable(),joinMeta.getOn());
            }
        }

    }




}
