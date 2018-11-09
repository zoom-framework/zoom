package org.zoomdev.zoom.dao.adapters;


/**
 *
 * 场景：
 *
 *  class Student{
 *      private String birthTime;  // 格式为 2000-10-10 10:10:10  ,但是数据库里面保存的格式是 2000101010101010,这个时候用于数据格式转化
 *  }
 *
 *  class Type{
 *      private Set<String> list;  // Set<String> 数据库中为  1,2,3,4,4 并且为Clob类型，那么这个时候为了在数据库和操作系统中统一使用数据标准，
 *      统一使用String作为中间的类型，
 *      查询：数据库的Clob拿出来转成String  然后再由String 抓成Set，
 *      入库：Set转成String，然后入库的时候转成Clob,
 *      DataAdapter=>    toDbValue          =>        StringUtils.join(list,",")
 *                       toEntityValue      =>        StringUtils.split(str,",")
 *                       getNeedsType       =>        String.class
 *  }
 *
 *
 *
 * @param <ENTITY_VALUE>
 * @param <DATABASE_VALUE>
 */
public interface DataAdapter<ENTITY_VALUE,DATABASE_VALUE>{


    /**
     * 将实体的字段值转成数据库的值
     * @param value
     * @return
     */
    DATABASE_VALUE toDbValue(ENTITY_VALUE value);

    /**
     * 将数据库的值转成实体类的值
     * @param value
     * @return
     */
    ENTITY_VALUE toEntityValue(DATABASE_VALUE value);


}
