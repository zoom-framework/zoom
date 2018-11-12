package org.zoomdev.zoom.timer;


public interface TimerService {


    /**
     * 增加一项自动任务
     *
     * @param jobName
     * @param jobClass
     * @param data
     * @param cron
     */
    <T> void startTimer(String jobName, Class<? extends TimerJob<T>> jobClass, T data, String cron);

    /**
     * 停止自动任务
     *
     * @param jobName
     */
    void stopTimer(String jobName);

    /**
     * 停止所有定时任务
     */
    void stopAll();
}
