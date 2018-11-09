package com.jzoom.zoom.common.filter.impl;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;

public class ClassNameFilter implements Filter<Class<?>>,Destroyable {

	private Filter<String> filter;
	
	public ClassNameFilter(String pattern) {
		this.filter = PatternFilterFactory.createFilter(pattern);
	}

	@Override
	public boolean accept(Class<?> value) {
		return filter.accept(value.getName());
	}

	@Override
	public void destroy() {
		this.filter = null;
	}

}
