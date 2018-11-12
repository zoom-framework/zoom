package org.zoomdev.zoom.async;

/**
 * 任务结果,一般用于提交一组任务
 *
 * @param <R>
 * @author renxueliang
 * @see JobQueue#execute(java.util.Iterator, JobExecutor, JobResult)
 */
public interface JobResult<R> {

    /**
     * 返回结果
     *
     * @param result
     */
    void onResult(R result);
}
