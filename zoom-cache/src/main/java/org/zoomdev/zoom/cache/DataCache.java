package org.zoomdev.zoom.cache;

import java.util.concurrent.TimeUnit;

/**
 * 缓存接口
 * @author jzoom
 *
 * @param <T>
 */
public interface DataCache {
	
	
	/**
	 * 根据key返回值
	 * @param key
	 * @return
	 */
	Object get(String key);
	
	/**
	 * 设置键值,默认30分钟
	 * @param key
	 * @param value
	 */
	Object set(String key,Object value);
	
	/**
	 * 设置有过期时间的键值
	 * @param key
	 * @param value
	 * @param unit
	 * @param timeout
	 */
	Object set(String key,Object value,TimeUnit unit,int timeout);
	
	/**
	 * 设置有过期时间的键值
	 * @param key								键
	 * @param value								值
	 * @param timeoutMs							超时毫秒
	 */
	Object set(String key,Object value,int timeoutMs);
	/**
	 * 移除key
	 * @param key
	 */
	void remove(String key);
}
