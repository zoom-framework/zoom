package com.jzoom.zoom.web.view.impl;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.web.utils.ResponseUtils;
import com.jzoom.zoom.web.view.View;


/**
 * 渲染文件
 * @author jzoom
 *
 */
public class FileView implements View {

	private File file;
	private String name;

	public FileView(File file) {
		this(file, file.getName());
	}
	
	public FileView(File file,String name) {
		assert(file!=null && name!=null);
		this.file = file;
		this.name = name;
	}

	@Override
	public void render( HttpServletResponse response) throws Exception {
		response.setHeader("Content-Disposition", new StringBuilder().append( "attachment; filename=")
				.append(URLEncoder.encode(name, response.getCharacterEncoding())).toString() );
		FileInputStream inputStream = new FileInputStream(file);
		int length = inputStream.available();
		response.setHeader("Content-Length", String.valueOf(length));
		try {
			ResponseUtils.write(response, inputStream, length);
		} finally {
			Io.close(inputStream);
		}

	}

}
