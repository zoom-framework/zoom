package com.jzoom.zoom.web.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.common.json.JSON;

public class ResponseUtils {
	
	private static final Log log = LogFactory.getLog(ResponseUtils.class);
	
	/**
	 * 使用write输出
	 * @param response
	 * @param content
	 */
	public static void write(HttpServletResponse response,String content) {
		Writer writer = null;
		try {
			 writer = response.getWriter();
			 writer.write(content);
		} catch (IOException e) {
			log.error("HttpServletResponse输出发生异常",e);
		}finally {
			Io.close(writer);
		}
		
	}
	public static void json(HttpServletResponse response, Object data) {
		OutputStream writer = null;
		try {
			 writer = response.getOutputStream();
			JSON.write(writer, data);
		} catch (IOException e) {
			log.error("HttpServletResponse输出发生异常",e);
		}finally {
			Io.close(writer);
		}
	}
	public static void write(HttpServletResponse response, byte[] bytes) {
		OutputStream writer = null;
		try {
			 writer = response.getOutputStream();
			 writer.write(bytes);
		} catch (IOException e) {
			log.error("HttpServletResponse输出发生异常",e);
		}finally {
			Io.close(writer);
		}
	}

	public static void write(HttpServletResponse response, FileInputStream inputStream, int length) {
		
		OutputStream writer = null;
		try {
			
			 writer = response.getOutputStream();
			 byte[] buffer = new byte[4096];
			 int total = 0;
			 while( total < length) {
				 int readed = Io.read(inputStream, buffer);
				 if(readed <= 0) {
					 break;
				 }
				 total += readed;
				 writer.write(buffer,0,readed);
			 } 
		} catch (IOException e) {
			log.error("HttpServletResponse输出发生异常",e);
		}finally {
			Io.close(writer);
		}
	}



}
