package com.jzoom.zoom.web.parameter.adapter.impl.map;

import java.util.Map;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;


public class NamedMapParameterAdapter implements ParameterAdapter<Map<String, Object>>{
	
	public static final NamedMapParameterAdapter ADAPTER = new NamedMapParameterAdapter();
	
	public NamedMapParameterAdapter() {
		
	}

	@Override
	public Object get(ActionContext context, Map<String, Object> data,String name,Class<?> type) {
		return data.get(name);
	}
	
}