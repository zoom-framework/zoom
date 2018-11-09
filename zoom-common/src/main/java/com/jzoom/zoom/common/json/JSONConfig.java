package com.jzoom.zoom.common.json;

import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONConfig {
	
	@SuppressWarnings("unchecked")
	public static <T> T load( InputStream is ,Class<T> classOfT) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_COMMENTS, true);

		
		try {
			return mapper.readValue(is, classOfT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		
	}

}
