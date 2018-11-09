package com.jzoom.zoom.web.rendering.impl;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.resp.JsonResponseAdapter;
import com.jzoom.zoom.web.utils.ResponseUtils;

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
