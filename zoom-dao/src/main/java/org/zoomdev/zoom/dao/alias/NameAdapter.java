package org.zoomdev.zoom.dao.alias;

/**
 * 名称适配器
 * 如果只有一张表，对于表 t, 字段 id,name,success_date 来说映射成为  id,name,successDate
 * 如果有多张表，对于表t_a和t_b, 分别有字段 id,name,success_date 来说映射成为 aId,aName,aSuccessDate与bId,bName,bSuccessDate
 *
 * {@link org.zoomdev.zoom.dao.alias.impl.CamelNameAdapter}
 * {@link org.zoomdev.zoom.dao.alias.impl.CamelAliasPolicy}
 * {@link org.zoomdev.zoom.dao.alias.impl.EmptyNameAdapter}
 * {@link org.zoomdev.zoom.dao.alias.impl.EmptyNameAdapter}
 * {@link org.zoomdev.zoom.dao.alias.impl.ToLowerCaseNameAdapter}
 * {@link org.zoomdev.zoom.dao.alias.impl.ToLowerCaseAiiasPolicy}
 * {@link org.zoomdev.zoom.dao.alias.impl.DetectPrefixAliasPolicyFactory}
 * {@link org.zoomdev.zoom.dao.alias.impl.MapNameAdapter}
 * {@link org.zoomdev.zoom.dao.alias.impl.PrefixAliasPolicy}
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
