package org.zoomdev.zoom.dao.alias;

/**
 * 名称适配器
 * 如果只有一张表，对于表 t, 字段 id,name,success_date 来说映射成为  id,name,successDate
 * 如果有多张表，对于表t_a和t_b, 分别有字段 id,name,success_date 来说映射成为 aId,aName,aSuccessDate与bId,bName,bSuccessDate
 *
 * @author jzoom
 */
public interface NameAdapter {


    /**
     * 从数据库列名称获取实体类字段名称,
     * 在多表的情况下，要获取原来的表名称，需要使用getOrgFieldName
     *
     * @param column
     * @return
     */
    String getFieldName(String column);

    /**
     * 从实体类字段名称获取数据库名称
     *
     * @param field
     * @return
     */
    String getColumnName(String field);

}
