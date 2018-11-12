package org.zoomdev.zoom.timer;

public interface TimerJob<T> {
    void execute(T data);
}
