package org.zoomdev.zoom.dao.driver;

import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.List;
import java.util.Map;

/**
 * 数据库快照，目前版本包含：
 * 1、表
 * 2、触发器
 * 3、序列
 */
public interface Snapshot {

    /**
     * 比较两个版本，并得出对应的sql语句
     * @param snapshot
     * @return
     */
    String compare(Snapshot snapshot);

    /**
     * 转成sql语句
     * @param driver
     * @return
     */
    String toSql(SqlDriver driver);

}
