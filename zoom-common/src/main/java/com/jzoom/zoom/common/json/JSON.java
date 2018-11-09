package com.jzoom.zoom.common.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.jzoom.zoom.common.io.Io;

public class JSON {

	static ObjectMapper mapper = new ObjectMapper();

	/**
	 * mapper
	 * 
	 * @return
	 */
	public static ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * 格式化json
	 * 
	 * @param value
	 * @return
	 */
	public static String stringify(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void write( OutputStream os , Object value ) {
		try {
			mapper.writeValue(os, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			Io.close(os);
		}
	}

	/**
	 * 
	 * 解析
	 * 
	 * @param src
	 * @param classOfT
	 * @return
	 */
	public static <T> T parse(String src, Class<T> classOfT) {
		try {
			return mapper.readValue(src, classOfT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static <T> T parse(String src, TypeReference<T> classOfT) {
		try {
			return mapper.readValue(src, classOfT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T checkParse(String src,Class<T> classOfT) {
		if(src == null)return null;
		return parse(src, classOfT);
	}
	
	/**
	 * 
	 * @param src
	 * @param classOfT
	 * @return
	 */
	public static <T> T parse(InputStream src, Class<T> classOfT) {
		try {
			return mapper.readValue(src, classOfT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param src
	 * @param classOfT
	 * @return
	 */
	public static <T> T parse(Reader src, Class<T> classOfT) {
		try {
			return mapper.readValue(src, classOfT);
		} catch (Exception e) {
			throw new RuntimeException("从reader解析json失败",e);
		}
	}
}
