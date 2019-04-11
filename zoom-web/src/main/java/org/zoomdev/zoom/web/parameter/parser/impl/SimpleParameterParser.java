package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.http.Destroyable;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserContainer;

class SimpleParameterParser implements ParameterParser, Destroyable {


    private ParameterParserContainer[] proxys;

    public SimpleParameterParser(
            ParameterParserContainer[] proxys
    ) {
        this.proxys = proxys;
    }


    @Override
    public void destroy() {

    }


    @Override
    public Object[] parse(ActionContext context) throws Exception {
        for (ParameterParserContainer parameterAdapterFactory : proxys) {
            if (parameterAdapterFactory.shouldAdapt(context)) {
                return parameterAdapterFactory.parse(context);
            }
        }
        throw new RuntimeException("不支持的解析参数类型");
    }


}
