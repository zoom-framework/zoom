package com.jzoom.zoom.common.utils;

import java.util.regex.Pattern;

/**
 * 提供一些常用的合法值的匹配，与具体业务无关
 * 
 * @author jzoom
 *
 */
public class ValidUtils {

	static final Pattern VARIABLE_PATTERN = Pattern.compile("^[a-zA-Z_$][a-zA-Z_0-9$]+");
	

	/**
	 * 是否是java变量名称，规则 含字母数字下划线$,必须以字母或下划线$开头
	 * 
	 * @return
	 */
	public static boolean isJavaVariableName(String src) {
		if(src==null)return false;
		return VARIABLE_PATTERN.matcher(src).matches();
	}

	/**
	 * 是否是一个java类名称
	 * @param src
	 * @return
	 */
	public static boolean isJavaClassName(String src) {
		if(src==null)return false;
		if(src.isEmpty())return false;
		if(src.endsWith("."))return false;
		if(src.startsWith("."))return false;
		String[] parts = src.split("\\.");
		if(parts.length==0)return false;
		for (String string : parts) {
			if(string.isEmpty()) {
				return false;
			}
			if(!isJavaVariableName(string)) {
				return false;
			}
		}
		return true;
	}
}
