package org.zoomdev.zoom.common.filter.pattern;

import org.zoomdev.zoom.common.filter.Filter;

/**
 * 匹配包含
 * @author jzoom
 *
 */
public class ContainsFilter implements Filter<String> {
	private String c;


	ContainsFilter(String c){
		this.c = c;
	}
	@Override
	public boolean accept(String value) {
		if(value==null)return false;
		return value.contains(c);
	}

}
