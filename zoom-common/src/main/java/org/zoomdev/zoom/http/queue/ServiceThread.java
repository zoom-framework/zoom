package org.zoomdev.zoom.http.queue;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 服务线程
 *
 * @author Randy
 */
public abstract class ServiceThread implements Runnable {


    private static final Log log = LogFactory.getLog(ServiceThread.class);

    /**
     * 是否正在运行
     */
    protected volatile boolean running;

    /**
     * 线程
     */
    protected Thread thread;

    public ServiceThread() {
        this.thread = new Thread(this);
        this.thread.setDaemon(true);
    }

    public void setName(String name) {
        this.thread.setName(name);
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * 启动
     */
    public synchronized void start() {
        if (running) return;
        running = true;
        thread.start();
    }


    /**
     * 停止
     */
    public synchronized void stop() {
        if (!running) return;
        running = false;
        thread.interrupt();
        while (thread.isAlive()) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract boolean repetitionRun();

    @Override
    public void run() {
        while (running) {
            try {
                if (!repetitionRun()) break;
            } catch (Throwable e) {
                log.warn("Service exception:", e);
            }
        }
    }
}
