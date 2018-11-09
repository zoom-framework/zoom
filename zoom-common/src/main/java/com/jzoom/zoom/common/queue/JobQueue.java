package com.jzoom.zoom.common.queue;


/**
 * 任务队列
 * @author Randy
 *
 * @param <T>
 */
public interface JobQueue<T>{
	
	/**
	 * 增加任务
	 * @param job
	 */
	void add(T job);
	
	
}
