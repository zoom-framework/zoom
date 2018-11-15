package org.zoomdev.zoom.dao.auto;

import org.zoomdev.zoom.dao.adapters.EntityField;

/**
 * 描述一个自动生成的字段
 */
public interface AutoField {

    /**
     * 如果有值，那么就不自动生成
     *
     * @return
     */
    boolean notGenerateWhenHasValue();

    /**
     * 是否是数据库自动生成
     *  用于 prepareStatement(sql,  new String[]{ keys })
     *
     * @return
     */
    boolean isDatabaseGeneratedKey();


    /**
     * 用于insert，比如 select xxx.next_val() from dual
     *
     * @return
     */
    String getSqlInsert(Object entity, EntityField entityField);

    /**
     * 某些情况下可以直接使用程序自动生成值
     *
     * @return
     */
    Object generateValue(Object entity, EntityField entityField);
}
