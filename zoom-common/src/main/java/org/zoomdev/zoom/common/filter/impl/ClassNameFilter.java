package org.zoomdev.zoom.common.filter.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;

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
