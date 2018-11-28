package org.zoomdev.zoom.dao.driver;

import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.impl.TableBuildInfo;

import java.util.List;

/**
 * oracle对于自增长的支持
 * <p>
 * 1、创建表的时候是否对应创建自增长trigger
 * 2、是否在判断自增长的时候使用trigger是否存在
 */
public interface AutoGenerateProvider {


    /**
     * 创建自增长的sql语句，比如利用trigger
     * @param table
     * @param autoColumn
     * @param sqlList
     */
    void buildAutoIncrease(TableBuildInfo table, ColumnMeta autoColumn, List<String> sqlList);


    /**
     * 创建自增长字段
     * @param dao
     * @param tableMeta
     * @param columnMeta
     * @return
     */
    AutoField createAutoField(Dao dao, TableMeta tableMeta, ColumnMeta columnMeta);

    /**
     * 本字段是否是自动的
     * @param dao
     * @param tableMeta
     * @param columnMeta
     * @return
     */
    boolean isAuto(Dao dao, TableMeta tableMeta, ColumnMeta columnMeta);
}
