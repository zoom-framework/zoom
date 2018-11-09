package org.zoomdev.zoom.web.view.impl;

import javax.servlet.http.HttpServletResponse;

import org.zoomdev.zoom.web.view.View;
import org.zoomdev.zoom.web.view.View;

public class RedirectView implements View {
	
	private String url;
	
	public RedirectView(String url) {
		this.url = url;
	}

	@Override
	public void render(HttpServletResponse response) throws Exception {
		response.sendRedirect(url);
	}

}
