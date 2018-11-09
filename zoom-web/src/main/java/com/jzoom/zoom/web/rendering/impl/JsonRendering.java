package com.jzoom.zoom.web.rendering.impl;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.utils.ResponseUtils;

public class JsonRendering implements Rendering {
	

	@Override
	public boolean render(ActionContext context) throws Exception {
		Object result = context.getRenderObject();
		HttpServletResponse response = context.getResponse();
		ResponseUtils.json(response, result);
		return true;
		
	}

}
