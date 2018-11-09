package com.jzoom.zoom.web.parameter.parser.impl;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.web.parameter.ParameterParser;
import com.jzoom.zoom.web.parameter.ParameterParserFactory;

import java.lang.reflect.Method;

public class SimpleParameterParserFactory implements ParameterParserFactory,Destroyable {

	@Override
	public ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names) {
		return new AutoParameterParser(controllerClass, method, names);
	}

	@Override
	public void destroy() {
		
	}

}
