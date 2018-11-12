package org.zoomdev.zoom.async;

public interface JobExecutor<T, R> {
    R execute(T data) throws Exception;
}
