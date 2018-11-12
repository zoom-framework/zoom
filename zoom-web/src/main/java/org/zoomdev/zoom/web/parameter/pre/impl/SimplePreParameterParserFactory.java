package org.zoomdev.zoom.web.parameter.pre.impl;

import org.zoomdev.zoom.web.parameter.PreParameterParser;
import org.zoomdev.zoom.web.parameter.PreParameterParserFactory;

import java.lang.reflect.Method;

/**
 * 创建根据request header自动判断的{@link PreParameterParser}
 *
 * @author jzoom
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
