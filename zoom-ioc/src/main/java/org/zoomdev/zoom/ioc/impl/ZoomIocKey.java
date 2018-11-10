package org.zoomdev.zoom.ioc.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.zoomdev.zoom.ioc.IocKey;

public class ZoomIocKey implements IocKey{
	
	private String name;
	private Class<?> type;

	public ZoomIocKey(Class<?> type) {
		this(null,type);
	}
	
	public ZoomIocKey(String name,Class<?> type) {
		if(StringUtils.isEmpty(name)) {
			name = null;
		}
		this.name = name;
		this.type = type;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	int h ;
	@Override
	public int hashCode() {
		int h = this.h;
		if(h==0) {
			h = 31 * h + type.hashCode();
			//h = 31 * h + classLoader.hashCode();
			if(name!=null) {
				h = 31 * h + name.hashCode();
			}
			this.h = h;
		}
		return h;
	}


	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof ZoomIocKey ) ) {
			return false;
		}
		
		ZoomIocKey key= (ZoomIocKey)obj;
	
		
//		if(classLoader != key.classLoader) {
//			return false;
//		}
		
		if(type != key.type) {
			return false;
		}
		
		if(!ObjectUtils.equals(name,key.name)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return type + " : " + name;
	}
	@Override
	public boolean hasName() {
		return name != null && !name.isEmpty();
	}
	@Override
	public boolean isInterface() {
		return type.isInterface();
	}
}
