package com.jzoom.zoom.web.parameter.pre.impl;

import java.lang.reflect.Method;

import com.jzoom.zoom.web.parameter.PreParameterParser;
import com.jzoom.zoom.web.parameter.PreParameterParserFactory;

/**
 * 创建根据request header自动判断的{@link PreParameterParser}
 * @author jzoom
 *
 */
public class SimplePreParameterParserFactory implements PreParameterParserFactory {
	protected static PreParameterParser parameterParser = new GroupPreParamParser(
			new JsonPreParamParser(),
			new UploadPreParamParser(),
			new XmlPreParamParser(), 
			new FormPreParamParser());
	@Override
	public PreParameterParser createPreParameterParser(Class<?> controllerClass, Method method) {
		
		return parameterParser;
	}

}
