package org.zoomdev.zoom.web.rendering.impl;

import javax.servlet.http.HttpServletResponse;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.utils.ResponseUtils;

public class JsonRendering implements Rendering {
	

	@Override
	public boolean render(ActionContext context) throws Exception {
		Object result = context.getRenderObject();
		HttpServletResponse response = context.getResponse();
		ResponseUtils.json(response, result);
		return true;
		
	}

}
