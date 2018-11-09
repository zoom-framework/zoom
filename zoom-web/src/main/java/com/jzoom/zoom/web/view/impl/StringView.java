package com.jzoom.zoom.web.view.impl;

import com.jzoom.zoom.web.view.View;

import javax.servlet.http.HttpServletResponse;

public class StringView implements View {
	
	private String str;
	
	public StringView( String str ) {
		this.str = str;
	}

	@Override
	public void render( HttpServletResponse response) throws Exception {
		response.getWriter().print(str);
	}

	public String getStr() {
		return str;
	}

}
