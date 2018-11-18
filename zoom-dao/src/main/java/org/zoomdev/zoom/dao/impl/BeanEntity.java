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
               Map<String, String> namesMap,
               JoinMeta[] joins) {
        super(table, entityFields, primaryKeys, autoEntity, namesMap);
        /**
         * group by 不需要主键,这段先注释下
         */
//        if (primaryKeys.length == 0) {
//            throw new DaoException("绑定实体类" + type + "，在这个类的所有字段(Field)中无法找到数据库中的对应主键，解决方法有：\n" +
//                    "1、增加数据库主键对应的字段\n" +
//                    "2、在字段上标注主键PrimaryKey,本配置可以忽略数据库主键" );
//        }
        this.joins = joins;
        this.type = type;

        if (joins != null) {
            for (JoinMeta join : joins) {

                join.setOn(parseOn(join.getOn()));
            }
        }


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
            throw new DaoException("初始化类失败" + type, e);
        }
    }

    @Override
    public void setQuerySource(SqlBuilder builder) {
        builder.table(table);
        if (joins != null) {
            for (JoinMeta joinMeta : joins) {
                builder.join(joinMeta.getTable(), joinMeta.getOn());
            }
        }

    }


}
