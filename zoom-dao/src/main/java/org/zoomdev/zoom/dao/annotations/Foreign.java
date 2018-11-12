package org.zoomdev.zoom.dao.annotations;

import org.zoomdev.zoom.dao.Relation;

/**
 * 描述一个外键
 * <p>
 * 使用方式
 * （1）
 * <p>
 * 如：
 *
 * @Table(name="room") class Room{
 * <p>
 * String buildingId;
 * <p>
 * }
 * @Table(name="building") class Building{
 * <p>
 * String id;
 * @Foreign(column="buildingId") List<Room> rooms;
 * <p>
 * }
 * <p>
 * <p>
 * （2）
 */
public @interface Foreign {

    //本表的哪个字段和其他表的意义相同
    String table();

    String column();

    /**
     * 关系
     *
     * @return
     */
    Relation relation();
}
