package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.adapters.EntityField;
import org.zoomdev.zoom.dao.meta.JoinMeta;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

class BeanEntity<T> extends AbstractEntity<T> {

    Class<T> type;

    Constructor<?> constructor;

    private JoinMeta[] joins;


    BeanEntity(String table,
               EntityField[] entityFields,
               EntityField[] primaryKeys,
               AutoEntity autoEntity,
               Class<T> type,
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


        constructor = Classes.findNoneParameterConstructor(type);
        if (constructor == null) {
            throw new DaoException("不支持绑定本实体类" + type + " 找不到无参且public的构造函数");
        }


        if (joins != null) {
            for (JoinMeta join : joins) {

                join.setOn(parseOn(join.getOn()));
            }
        }


    }

    @Override
    public Class<T> getType() {
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
                builder.join(joinMeta.getTable(), joinMeta.getOn(), joinMeta.getType());
            }
        }

    }

    @Override
    public List<EntityField> select(List<EntityField> holder, Iterable<String> fields) {
        for (String key : fields) {
            EntityField entityField = tryToFind(key);
            if (entityField == null) {
                throw new DaoException("找不到" + key + "对应的字段,所有可能的字段为" + StringUtils.join(getAvailableFields()));
            }
            holder.add(entityField);
        }

        return holder;
    }


}
