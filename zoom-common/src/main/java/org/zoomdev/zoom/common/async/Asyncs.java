package org.zoomdev.zoom.common.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 提供对外的factory
 * @author jzoom
 *
 */
public class Asyncs {


    static AsyncService service = new AsyncService(20);


    public static <T> Future<T> delay(final int ms, final T result) {
        return service.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                Thread.sleep(ms);
                return result;
            }
        });
    }
	
	/**
	 * 默认异步任务队列
	 * 
	 * @return
	 */
	public static JobQueue defaultJobQueue() {
		return service;
	}
	
	/**
	 * 新的异步任务队列
	 * @param threadCount
	 * @return
	 */
	public static JobQueue newJobQueue(int threadCount) {
		return new AsyncService(threadCount);
	}
}
