package com.jzoom.zoom.dao;

/**
 * 管理Entity ，table和实体类的绑定关系，注意：
 * 1、一个实体类支持绑定多张表
 * 2、默认情况下是根据  {@link com.jzoom.zoom.dao.annotations.Table} 来绑定
 * @author jzoom
 *
 */
public interface EntityFactory {

	/**
	 * 根据实体类和表，获取到一个Entity绑定关系,
	 * @param dao
	 * @param type
	 * @return
	 */
	Entity getEntity(Dao dao,Class<?> type);


	/**
	 * 根据实体类和表，获取到一个Entity绑定关系,
	 * @param dao
	 * @param type
	 * @param table  一个实体类允许绑定多个表
	 * @return
	 */
    Entity getEntity(Dao dao, Class<?> type, String table);

    /**
     * 根据实体类和表，获取到一个Entity绑定关系,
     *
     * @param dao
     * @param type
     * @param tables
     * @return
     */
    Entity getEntity(Dao dao, Class<?> type, String[] tables);

}
