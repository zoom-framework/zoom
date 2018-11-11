package org.zoomdev.zoom.dao;

import java.util.HashMap;
import java.util.Map;

import org.zoomdev.zoom.common.utils.DataObject;
import org.zoomdev.zoom.common.utils.MapUtils;

public class Record extends DataObject {

	public static Record asRecord(Object...values){

        if(values.length % 2 != 0) {
            throw new RuntimeException("参数个数必须为2的倍数");
        }
        Record data = new Record();
        for(int i=0 ,c = values.length; i < c; i+=2) {
            data.put( (String) values[i], values[i+1]);
        }

        return data;
	}






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
