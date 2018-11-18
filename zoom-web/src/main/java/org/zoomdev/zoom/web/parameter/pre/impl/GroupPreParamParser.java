package org.zoomdev.zoom.web.parameter.pre.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.PreParameterParser;

public class GroupPreParamParser implements PreParameterParser {

    private PreParameterParser[] parsers;

    public GroupPreParamParser(PreParameterParser... parsers) {
        this.parsers = parsers;
    }

    @Override
    public Object preParse(ActionContext context) throws Exception {
        String contentType = context.getRequest().getContentType();

        for (PreParameterParser preParameterParser : parsers) {
            if (preParameterParser.shouldParse(contentType)) {
                return preParameterParser.preParse(context);
            }
        }

        throw new RuntimeException("找不到参数解析器");
    }

    @Override
    public boolean shouldParse(String contentType) {
        return true;
    }

}
