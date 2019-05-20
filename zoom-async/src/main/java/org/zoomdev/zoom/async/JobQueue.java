package org.zoomdev.zoom.async;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * submit : 返回Future
 * run : 任务发送到任务队列，然后脱离控制（异步）
 * execute  直接执行，同步,一般用于多个任务，需要等待的情况
 *
 * @author renxueliang
 */
public interface JobQueue {

    interface Response<R>{
        R get();
        Throwable error();
        boolean isSuccess();
    }


    /**
     * 任务队列,可将任务投递到指定的线程中执行，
     * 如订单处理等，这样，使用一些hash算法，可避免处理同步问题。
     *
     * @param data
     * @return
     */
    <R, T> Future<R> submit(T data, JobExecutor<T, R> executor);


    /**
     * 提交多组任务，每一组任务含多个任务,直
     * 到这些任务做完为止,并在一组任务做完之后，的到通知
     * 这个方法本身是同步的，会等待所有任务执行完毕
     *
     * @param it
     * @param executor
     * @param result
     * @throws ExecutionException
     * @throws InterruptedException
     */
    <T, R> void execute(Iterator<List<T>> it,
                        JobExecutor<T, R> executor,
                        JobResult<List<R>> result)
            throws ExecutionException, InterruptedException;

    /**
     * 提交多个任务,直到做完为止，并返回执行结果,
     * 这个任务本身同步
     *
     * @param jobs
     * @param executor
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    <T, R> List<R> execute(Iterable<T> jobs,
                           final JobExecutor<T, R> executor)
            throws ExecutionException, InterruptedException;


    /**
     * 异步执行队列，且保证队列的执行顺序
     * @param <T>
     * @param <R>
     * @param jobs
     * @param executor
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    <T, R> Future<List<R>> sequence(Iterable<T> jobs,
                                    final JobExecutor<T, R> executor)
            throws ExecutionException, InterruptedException;

    /**
     * 使用指定的任务处理器处理任务
     *
     * @param data
     * @param handler
     */
    <T> void run(T data, final JobHandler<T> handler);


    /**
     * 与java原生Executor的作用一致，返回Future
     *
     * @param callable
     * @param <T>
     * @return
     */
    <T> Future<T> submit(Callable<T> callable);


    /**
     * 直接执行一个Runnable,不会等待Runnable执行结束
     *
     * @param runnable
     */
    void run(Runnable runnable);


    /////////// 注册

    /**
     * 使用注册过的任务处理器执行异步任务
     *
     * @param name
     * @param data
     */
    void run(String name, Object data);


    /**
     * 注册一个任务处理器
     *
     * @param name
     * @param handler
     */
    <T> void register(String name, JobHandler<T> handler);

    /**
     * 移除
     *
     * @param name
     */
    void unregister(String name);



}
