package org.zoomdev.zoom.dao;

import java.util.Map;

import org.zoomdev.zoom.common.utils.DataObject;

public class Record extends DataObject {
	
	

	public Record(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	public Record() {
		super();
	}

	public Record(int initialCapacity) {
		super(initialCapacity);
	}
	
	
	public Record set(String key,Object value) {
		put(key, value);
		return this;
	}
	
	public Record setAll(Map<String, Object> data) {
		assert(data!=null);
		putAll(data);
		return this;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8983230317786957763L;

	
}
