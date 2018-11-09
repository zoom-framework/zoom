package com.jzoom.zoom.web.view.impl;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.view.View;

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
