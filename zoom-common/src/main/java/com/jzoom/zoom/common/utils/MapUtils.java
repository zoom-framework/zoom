package com.jzoom.zoom.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


public class MapUtils {

	/**
	 * 方便的创建一个Map,使用方法: MapUtils.asMap(key0,value0,key1,value1......)
	 * @param values
	 * @return
	 */
	public static Map<String, Object> asMap( Object...values ){
		
		if(values.length % 2 != 0) {
			throw new RuntimeException("参数个数必须为2的倍数");
		}
		Map<String, Object> data = new HashMap<String, Object>();
		for(int i=0 ,c = values.length; i < c; i+=2) {
			data.put( (String) values[i], values[i+1]);
		}
		
		return data;
		
	}

	/**
	 * 将一个list转成id指定的key对应的map
	 * @param list
	 * @param id
	 * @return
	 */
	public static Map<?, ?> toMap(List<? extends Map<String,?>> list,Object id) {
		Map<String, Map<?, ?>> result  = new HashMap<String,Map<?,?>>(list.size());
		for (Map<String, ?> map : list) {
			result.put( (String) map.get(id), map);
		}
		
		return result;
	}
	
	
	public static Map<String, String> toKeyAndLabel( List<? extends Map<String,?>> list,Object id,Object label ){
		Map<String, String> result  = new HashMap<String,String>(list.size());
		for (Map<String, ?> map : list) {
			result.put( (String) map.get(id), (String)map.get(label));
		}
		
		return result;
	}


    public static <V> void createIfAbsent(Map<String, Object> map, Callable<V> callable) {

    }


	
}
