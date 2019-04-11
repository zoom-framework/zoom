package org.zoomdev.zoom.async.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.async.JobExecutor;
import org.zoomdev.zoom.async.JobHandler;
import org.zoomdev.zoom.async.JobQueue;
import org.zoomdev.zoom.async.JobResult;
import org.zoomdev.zoom.http.Destroyable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务服务
 *
 * @author renxueliang
 */
class AsyncService implements JobQueue, ThreadFactory, Destroyable {


    private Map<String, JobHandler<?>> map = new ConcurrentHashMap<String, JobHandler<?>>();

    private final Log logger = LogFactory.getLog(getClass());

    private ExecutorService service;


    /**
     * @param threadCount
     */
    AsyncService(int threadCount) {
        service = Executors.newFixedThreadPool(threadCount, this);
    }


    @Override
    public void destroy() {
        if (service != null) {
            service.shutdown();
            boolean waitFor;
            do {
                try {
                    waitFor = service.awaitTermination(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    break;
                }
            } while (!waitFor);
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
    public <T, R> void execute(Iterator<List<T>> it, JobExecutor<T, R> executor, JobResult<List<R>> result)
            throws ExecutionException, InterruptedException {
        while (it.hasNext()) {
            List<R> r = execute(it.next(), executor);
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
    public <T, R> List<R> execute(Iterable<T> jobs, final JobExecutor<T, R> executor)
            throws ExecutionException, InterruptedException {
        List<Future<R>> tmps = new ArrayList<Future<R>>();
        List<R> result = new ArrayList<R>();
        for (T job : jobs) {
            tmps.add(service.submit(new InnerJobExecutor<T, R>(job, executor)));
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
     */
    public <T> void register(String name, JobHandler<T> handler) {
        map.put(name, handler);
    }

    @Override
    public void unregister(String name) {
        map.remove(name);
    }


    /**
     * 使用指定的任务处理器处理一项任务
     *
     * @param data
     * @param handler
     */
    public <T> void run(final T data, final JobHandler<T> handler) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                handler.execute(data);
            }
        });
    }

    /**
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
    public void run(final String name, final Object data) {
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
        thread.setName("Async-[" + count.getAndIncrement() + "]");
        return thread;
    }

    /**
     *
     */
    @Override
    public <R, T> Future<R> submit(T data, JobExecutor<T, R> executor) {
        return service.submit(new InnerJobExecutor<T, R>(data, executor));
    }


    @Override
    public <T> Future<T> submit(Callable<T> callable) {

        return service.submit(callable);
    }


}
