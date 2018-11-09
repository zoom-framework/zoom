package com.jzoom.zoom.dao.alias;

/**
 * 对字段、表进行别名的策略
 * @author jzoom
 *
 */
public interface AliasPolicy {
	/**
	 * 根据实际名称获取别名
	 * @param column
	 * @return
	 */
	String getAlias(String column);
	
	
}
