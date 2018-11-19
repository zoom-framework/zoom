package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterParser;

/**
 * 0个参数使用本解析器
 */
public class EmptyParamterParser implements ParameterParser {

    public static final ParameterParser DEFAULT = new EmptyParamterParser();

    private static final String[] EMPTY = new String[0];

    private EmptyParamterParser(){

    }

    @Override
    public Object[] parse(ActionContext context) throws Exception {
        return EMPTY;
    }


}
