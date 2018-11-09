package org.zoomdev.zoom.web.parameter.pre.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.PreParameterParser;

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
