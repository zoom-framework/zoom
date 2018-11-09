package org.zoomdev.zoom.common.async;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 
 * 
 * 
 * 
 * @author renxueliang
 *
 */
public interface JobQueue {
	
	/**
	 * 任务队列,可将任务投递到指定的线程中执行，
	 * 如订单处理等，这样，使用一些hash算法，可避免处理同步问题。
	 * @param data
	 * @return
	 */
	<R,T> Future<R> submit(T data,JobExecutor<T, R> executor);
	
	
	<T> Future<T> submit(Callable<T> callable);
	/**
	 * 
	 * @param data
	 * @param handler
	 * @param thread
	 */
	<T> void execute(T data,JobHandler<T> handler);
	
	
	/**
	 * 提交多组任务，每一组任务含多个任务,直到这些任务做完为止,并在一组任务做完之后，的到通知
	 * 
	 * 
	 * @param it
	 * @param executor
	 * @param result
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	<T, R> void submit(Iterator<List<T>> it, JobExecutor<T, R> executor, JobResult<List<R>> result)
			throws ExecutionException, InterruptedException;
	
	/**
	 * 提交多个任务,直到做完为止，并返回执行结果
	 * @param jobs
	 * @param executor
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	<T, R> List<R> submit(List<T> jobs, final JobExecutor<T, R> executor)
			throws ExecutionException, InterruptedException;
	
	
	/**
	 * 注册一个任务处理器
	 * @param name
	 * @param handler
	 */
	<T> void register(String name, JobHandler<T> handler);
	
	/**
	 * 使用指定的任务处理器处理任务
	 * @param data
	 * @param handler
	 */
	<T> void executeSimple(T data,final JobHandler<T> handler);
	
	/**
	 * 执行异步任务
	 * @param name
	 * @param data
	 */
	void execute(String name, final Object data);


	void run(Runnable runnable);
}
