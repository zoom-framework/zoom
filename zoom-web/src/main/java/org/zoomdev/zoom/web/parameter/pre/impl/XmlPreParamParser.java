package org.zoomdev.zoom.web.parameter.pre.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.PreParameterParser;

public class XmlPreParamParser implements PreParameterParser {

    @Override
    public Object preParse(ActionContext context) throws Exception {

        return null;
    }

    @Override
    public boolean shouldParse(String contentType) {
        return contentType.startsWith("application/xml");
    }

}
