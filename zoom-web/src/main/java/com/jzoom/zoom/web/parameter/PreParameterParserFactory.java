package com.jzoom.zoom.web.parameter;

import java.lang.reflect.Method;


public interface PreParameterParserFactory {
	PreParameterParser createPreParameterParser(Class<?> controllerClass, Method method);
}
