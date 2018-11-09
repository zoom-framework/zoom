package org.zoomdev.zoom.dao.annotations;

import org.zoomdev.zoom.dao.Relation;

/**
 * 描述一个外键
 *
 * 使用方式
 * （1）
 *
 *  如：
 *
 * @Table(name="room")
 *  class Room{
 *
 *      String buildingId;
 *
 *  }
 *
 * @Table(name="building")
 *  class Building{
 *
 *     String id;
 *
 * @Foreign(column="buildingId")
 *     List<Room> rooms;
 *
 *  }
 *
 *
 * （2）
 *
 *
 */
public @interface Foreign {

    //本表的哪个字段和其他表的意义相同
    String table();

    String column();

    /**
     * 关系
     * @return
     */
    Relation relation();
}
