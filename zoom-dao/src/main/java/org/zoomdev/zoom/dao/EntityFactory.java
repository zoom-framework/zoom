package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.dao.annotations.Table;

import java.util.Map;

/**
 * 管理Entity ，table和实体类的绑定关系，注意：
 * 1、一个实体类支持绑定多张表
 * 2、默认情况下是根据  {@link Table} 来绑定
 *
 * @author jzoom
 */
public interface EntityFactory {

    /**
     * 根据实体类和表，获取到一个Entity绑定关系,
     *
     * @param type
     * @return
     */
    Entity getEntity(Class<?> type);


    /**
     * 根据实体类和表，获取到一个Entity绑定关系,
     *
     * @param tables
     * @return
     */
    Entity getEntity(String... tables);


    /**
     * 确定绑定关系，需要配置映射表
     * @param type
     * @param table
     * @param field2column   实体类字段到数据库字段的映射关系
     * @return
     */
    <T> Entity<T> bindEntity(Class<T> type,String table,Map<String,String> field2column);


    void clearCache();

}
