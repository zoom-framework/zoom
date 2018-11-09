package com.jzoom.zoom.common.async;

/**
 * 任务结果,一般用于提交一组任务
 *
 * @see JobQueue#submit(java.util.Iterator, JobExecutor, JobResult)
 * 
 * @author renxueliang
 *
 * @param <R>
 */
public interface JobResult<R> {
	
	/**
	 * 返回结果
	 * @param result
	 */
	void onResult(R result);
}
