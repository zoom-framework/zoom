package org.zoomdev.zoom.http.queue;


/**
 * 任务队列
 *
 * @param <T>
 * @author Randy
 */
public interface JobQueue<T> {

    /**
     * 增加任务
     *
     * @param job
     */
    void add(T job);


}
