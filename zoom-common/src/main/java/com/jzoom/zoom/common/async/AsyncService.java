package com.jzoom.zoom.common.async;

import com.jzoom.zoom.common.Destroyable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务服务
 * @author renxueliang
 *
 */
class AsyncService implements JobQueue, ThreadFactory,Destroyable {


	private Map<String, JobHandler<?>> map = new ConcurrentHashMap<String, JobHandler<?>>();
	
	private final Log logger = LogFactory.getLog(getClass());

	private ExecutorService service;

	
	/**
	 * 
	 * @param threadCount
	 */
	AsyncService(int threadCount) {
		service = Executors.newFixedThreadPool(threadCount, this);
	}


	@Override
	public void destroy() {
		if(service!=null) {
			service.shutdown();
			boolean waitFor;
			do{
				try {
					waitFor = service.awaitTermination(2, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					break;
				}
			}while(!waitFor);
		}
		
	}

	private static class InnerJobExecutor<T, R> implements Callable<R> {

		private T data;
		private JobExecutor<T, R> executor;

		public InnerJobExecutor(T data, JobExecutor<T, R> executor) {
			this.data = data;
			this.executor = executor;
		}

		@Override
		public R call() throws Exception {
			return executor.execute(data);
		}
	}

	/**
	 * 执行大量任务，并等待完成
	 * 
	 * @param it
	 * @param executor
	 * @param result
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public <T, R> void submit(Iterator<List<T>> it, JobExecutor<T, R> executor, JobResult<List<R>> result)
			throws ExecutionException, InterruptedException {
		while (it.hasNext()) {
			List<R> r = submit(it.next(), executor);
			result.onResult(r);
		}
	}

	/**
	 * 执行一组任务，并等待完成,每一个任务将返回结果
	 * 
	 * @param jobs
	 * @param executor
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public <T, R> List<R> submit(List<T> jobs, final JobExecutor<T, R> executor)
			throws ExecutionException, InterruptedException {
		List<Future<R>> tmps = new ArrayList<Future<R>>(jobs.size());
		List<R> result = new ArrayList<R>();
		for (int i = 0, c = jobs.size(); i < c; i++) {
			tmps.add(service.submit(new InnerJobExecutor<T, R>(jobs.get(i), executor)));
		}
		for (Future<R> f : tmps) {
			result.add(f.get());
		}
		return result;
	}

	/**
	 * Register async task executor
	 * 
	 * @param name
	 * @param executor
	 */
	public <T> void register(String name, JobHandler<T> handler) {
		map.put(name, handler);
	}

	
	
	/**
	 * 使用指定的任务处理器处理一项任务
	 * @param data
	 * @param handler
	 */
	public <T> void executeSimple(final T data,final JobHandler<T> handler) {
		service.execute(new Runnable() {
			@Override
			public void run() {
				handler.execute(data);
			}
		});
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected JobHandler getJobHandler(String name) {
		return map.get(name);
	}

	/**
	 * Execute an async task.
	 * 
	 * @param name
	 * @param data
	 */
	@SuppressWarnings("rawtypes")
	public void execute(final String name, final Object data) {
		service.execute(new Runnable() {
			
			/**
			 * 
			 */
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				JobHandler handler = getJobHandler(name);
				if (handler == null) {
					logger.fatal(String.format("Task handle for %s is not exists!", name));
					return;
				}
				handler.execute(data);
			}
		});
	}

	@Override
	public void run(Runnable runnable) {
		service.execute(runnable);
	}

	private AtomicInteger count = new AtomicInteger(0);
	
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		thread.setName("Async-["+count.getAndIncrement()+"]");
		return thread;
	}

	/**
	 * 这里暂时没有实现
	 */
	@Override
	public <R, T> Future<R> submit(T data, JobExecutor<T, R> executor) {
		return service.submit( new InnerJobExecutor<T, R>(data, executor) );
	}

	@Override
	public <T> void execute(T data, JobHandler<T> handler) {
		throw new RuntimeException("还没有实现，正在考虑使用场景");
	}

	@Override
	public <T> Future<T> submit(Callable<T> callable) {
		
		return service.submit(callable);
	}



}
