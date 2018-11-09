package com.jzoom.zoom.common.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayFilter {
	
	public static <T> Collection<T> filter(Collection<T> list,Filter<T> filter){
		List<T> result = new ArrayList<T>();
		for (T t : list) {
			if(filter.accept(t)) {
				result.add(t);
			}
		}
		return result;
		
	}
}
