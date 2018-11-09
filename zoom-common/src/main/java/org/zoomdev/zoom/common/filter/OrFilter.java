package org.zoomdev.zoom.common.filter;

/**
 * 所有过滤器中，只要有一个通过，就算通过
 * @author jzoom
 *
 * @param <T>
 */
public class OrFilter<T> implements Filter<T> {

	private Filter<T>[] filters;
	
	public OrFilter(Filter<T>...filters) {
		this.filters = filters;
	}
	
	@Override
	public boolean accept(T value) {
		for (Filter<T> filter : filters) {
			if(filter.accept(value)) {
				return true;
			}
		}
		return false;
	}

}
