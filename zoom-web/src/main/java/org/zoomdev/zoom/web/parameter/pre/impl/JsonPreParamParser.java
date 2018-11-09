package org.zoomdev.zoom.web.parameter.pre.impl;

import java.util.Map;

import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.PreParameterParser;


public class JsonPreParamParser implements PreParameterParser{

	@Override
	public Object preParse(ActionContext context) throws Exception {
		return JSON.parse(context.getRequest().getReader(), Map.class);
	}

	@Override
	public boolean shouldParse(String contentType) {
		if(contentType==null)return false;
		return contentType.startsWith("application/json");
	}
}