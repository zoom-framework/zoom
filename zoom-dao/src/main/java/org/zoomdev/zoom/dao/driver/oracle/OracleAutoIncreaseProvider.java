package org.zoomdev.zoom.dao.driver.oracle;

import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;

import java.util.List;

/**
 * oracle对于自增长的支持
 *
 * 1、创建表的时候是否对应创建自增长trigger
 * 2、是否在判断自增长的时候使用trigger是否存在
 *
 */
public interface OracleAutoIncreaseProvider {


    void buildAutoIncrease(TableBuildInfo table, ColumnMeta autoColumn,List<String> sqlList);
}
