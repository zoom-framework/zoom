package com.jzoom.zoom.web.parameter.parser.impl;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.BasicParameterAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.map.NamedMapParameterAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.map.PathMapParameterAdapter;
import com.jzoom.zoom.web.parameter.adapter.impl.map.RequestBodyMapAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class MapParameterParserFactory extends AbsParameterParserFactory<Map<String, Object>> {


	public MapParameterParserFactory( ) {
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ParameterAdapter<Map<String, Object>> createAdapter(String name,Class<?> type, Type genericType, Annotation[] annotations ) {
		
		if(isRequestBody(name, annotations)) {
			if(Map.class == type) {
				return (ParameterAdapter)BasicParameterAdapter.EQ;
			}
			return RequestBodyMapAdapter.ADAPTER;
		} else if(isPathVariable(name, annotations)) {
			//简单类型直接来
			return (ParameterAdapter)PathMapParameterAdapter.ADAPTER;
		} else {
			//简单类型直接来
			if(Classes.isSimple(type)) {
				return NamedMapParameterAdapter.ADAPTER;
			}else if(type.isArray()) {
				
			}else if( Collection.class.isAssignableFrom(type) ) {
				
				return NamedMapParameterAdapter.ADAPTER;
				
				
			}else if( Map.class.isAssignableFrom(type) ) {
				return NamedMapParameterAdapter.ADAPTER;
			}else {
				//按照bean处理
				return NamedMapParameterAdapter.ADAPTER;
			}
		}
		
		return null;
	}

}
