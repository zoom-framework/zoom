package org.zoomdev.zoom.common.el;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zoomdev.zoom.common.config.ConfigReader;

public class ElParser {
	private static Pattern EL_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9_]+)\\}");

	/**
	 * 解析配置   
	 * @param value 解析 ${config} 形式的字符串，并解析出变量名称
	 * @return
	 */
	public static String parseConfigValue(String value) {
		Matcher matcher = EL_PATTERN.matcher(value);
		if(matcher.matches()) {
			value = matcher.group(1);
			return ConfigReader.getDefault().getString(value);
		}
		return value;
	}
}
