package com.jzoom.zoom.web.parameter.pre.impl;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.PreParameterParser;

/**
 * 可以接受没有contentType, contentType= www-form
 * @author jzoom
 *
 */
public class FormPreParamParser implements PreParameterParser {

	@Override
	public Object preParse(ActionContext context) throws Exception {
		
		return context.getRequest();
	}

	@Override
	public boolean shouldParse(String contentType) {
		
		return true;
	}

}
