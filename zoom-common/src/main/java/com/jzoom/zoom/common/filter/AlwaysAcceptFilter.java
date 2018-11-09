package com.jzoom.zoom.common.filter;

public class AlwaysAcceptFilter<T> implements Filter<T> {

	@Override
	public boolean accept(T value) {
		return true;
	}

}
