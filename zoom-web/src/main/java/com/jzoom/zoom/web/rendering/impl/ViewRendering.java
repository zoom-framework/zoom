package com.jzoom.zoom.web.rendering.impl;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.view.View;

public class ViewRendering implements Rendering{

	@Override
	public boolean render(ActionContext context) throws Exception {
		if(context.getRenderObject() instanceof View) {
			((View)context.getResult()).render(context.getResponse());
			return true;
		}
		return false;
	}

}
