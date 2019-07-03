package org.zoomdev.zoom.dao;

/**
 * Bean和Table相关信息之间转换的适配器
 * 一般来说是解析这个Bean对应的标注来进行判断
 */
public interface BeanTableAdapter {

    /**
     * 从类中获取标注的信息
     *
     * @param type
     * @return
     */
    BeanTableInfo getTableInfo(Class<?> type);


    BeanTableInfo getTableInfo(Class<?> type, String table);

}
