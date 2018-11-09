package com.jzoom.zoom.common.config;
/**
 * 配置值解析器
 * 解析如${env:PATH}之类的值
 * @author jzoom
 *
 */
public interface ConfigValueParser {
	
	/**
	 * 解析配置值
	 * @param value		原始配置值
	 * @return			解析后的值
	 */
	Object parse(Object value);
}
