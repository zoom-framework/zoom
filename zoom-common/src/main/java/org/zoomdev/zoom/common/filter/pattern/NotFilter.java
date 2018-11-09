package org.zoomdev.zoom.common.filter.pattern;

import org.zoomdev.zoom.common.filter.Filter;

public class NotFilter<T> implements Filter<T> {
	
	private Filter<T> filter;
	
	public NotFilter(Filter<T> filter) {
		assert(filter!=null);
		this.filter = filter;
	}

	@Override
	public boolean accept(T value) {
		return !filter.accept(value);
	}

}
