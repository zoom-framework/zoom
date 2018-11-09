package com.jzoom.zoom.web.parameter.parser.impl;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.ParameterParser;

public class EmptyParamterParser implements ParameterParser {
	
	private static final String[] EMPTY = new String[0];

	@Override
	public Object[] parse(ActionContext context) throws Exception {
		return EMPTY;
	}


}
