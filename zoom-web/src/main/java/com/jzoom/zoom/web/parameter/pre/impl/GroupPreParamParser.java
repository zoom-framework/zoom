package com.jzoom.zoom.web.parameter.pre.impl;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.PreParameterParser;

public class GroupPreParamParser implements PreParameterParser{
	
	private PreParameterParser[] parsers;
	
	public GroupPreParamParser(PreParameterParser...parsers) {
		this.parsers = parsers;
	}

	@Override
	public Object preParse(ActionContext context) throws Exception {
		String contentType = context.getRequest().getHeader("Content-Type");
		
		for (PreParameterParser preParameterParser : parsers) {
			if(preParameterParser.shouldParse(contentType)) {
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
