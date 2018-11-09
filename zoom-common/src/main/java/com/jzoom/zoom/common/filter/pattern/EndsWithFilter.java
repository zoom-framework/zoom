package com.jzoom.zoom.common.filter.pattern;

import com.jzoom.zoom.common.filter.Filter;

public class EndsWithFilter implements Filter<String> {

	private String prefix;


	EndsWithFilter(String prefix){
		this.prefix = prefix;
	}
	
	
	@Override
	public boolean accept(String value) {
		if(value==null)return false;
		return value.endsWith(prefix);
	}

}
