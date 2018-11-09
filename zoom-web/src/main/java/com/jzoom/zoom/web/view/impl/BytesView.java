package com.jzoom.zoom.web.view.impl;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.web.utils.ResponseUtils;
import com.jzoom.zoom.web.view.View;

/**
 * 直接渲染字节流，以附件的方式下载
 * @author jzoom
 *
 */
public class BytesView implements View {

	private byte[] bytes;
	private String name;

	public BytesView(byte[] bytes,String name) {
		this.bytes = bytes;
		this.name = name;
	}


	@Override
	public void render(HttpServletResponse response) throws Exception {
		response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(name, "utf-8"));
		ResponseUtils.write(response, bytes);
	}

}
