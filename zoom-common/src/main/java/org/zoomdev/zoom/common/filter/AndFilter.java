package org.zoomdev.zoom.common.filter;

/**
 * 所有过滤器中，必须所有的过滤器都通过，才算通过
 * @author jzoom
 *
 * @param <T>
 */
public class AndFilter<T> implements Filter<T> {

	private Filter<T>[] filters;
	
	public AndFilter(Filter<T>...filters) {
		this.filters = filters;
	}
	
	@Override
	public boolean accept(T value) {
		for (Filter<T> filter : filters) {
			if(!filter.accept(value)) {
				return false;
			}
		}
		return true;
	}

}
