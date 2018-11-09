package com.jzoom.zoom.web.view.impl;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.view.View;

public class CacheControlView implements View {
	
	private final View proxy;
	private String cacheControl;
	
	public static CacheControlView staticResource(View proxy) {
		return new CacheControlView(proxy, "public");
	}
	
	
	private CacheControlView(View proxy,String cacheControl) {
		this.proxy = proxy;
		this.cacheControl = cacheControl;
	}

	@Override
	public void render(HttpServletResponse response) throws Exception {
		response.addHeader("Cache-Control", cacheControl);
		response.addHeader("Cache-Control", "max-age=31536000");
		proxy.render(response);
	}

}
