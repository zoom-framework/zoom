package com.jzoom.zoom.web.parameter.adapter.impl.map;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;

public class PathMapParameterAdapter implements ParameterAdapter<Object>{
	
	public static final PathMapParameterAdapter ADAPTER = new PathMapParameterAdapter();
	
	public PathMapParameterAdapter() {
		
	}

	@Override
	public Object get(ActionContext context, Object data,String name,Class<?> type) {
		return context.getRequest().getAttribute(name);
	}
	
}