package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;

public class RedirectRendering implements Rendering {
	public static final String REDIRECT = "redirect:";
	public static final String FORWARD = "forward:";

	@Override
	public boolean render(ActionContext context) throws Exception {
		if(context.getResult() instanceof String) {
			String result = (String) context.getResult();
			if(result.startsWith(RedirectRendering.REDIRECT)) {
				context.getResponse().sendRedirect(result.substring(RedirectRendering.REDIRECT.length()));
				return true;
			}else if(result.startsWith(FORWARD)) {
				throw new RuntimeException("目前这个版本还不支持forward");
			}
		}
		
		return false;
	}

}
