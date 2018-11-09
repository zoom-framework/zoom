package org.zoomdev.zoom.web.rendering.impl;

import javax.servlet.http.HttpServletResponse;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.resp.JsonResponseAdapter;
import org.zoomdev.zoom.web.utils.ResponseUtils;

public class AdaptedJsonRendering implements Rendering {
	
	private JsonResponseAdapter adapter;
	
	public AdaptedJsonRendering(JsonResponseAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public boolean render(ActionContext context) throws Exception {
		Object result = context.getResult();
		HttpServletResponse response = context.getResponse();
		result = adapter.adapterOk(result);
		ResponseUtils.json(response, result);
		return true;
		
	}


}
