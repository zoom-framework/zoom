package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterParser;

public class EmptyParamterParser implements ParameterParser {
	
	private static final String[] EMPTY = new String[0];

	@Override
	public Object[] parse(ActionContext context) throws Exception {
		return EMPTY;
	}


}
