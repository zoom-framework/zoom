package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;

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
