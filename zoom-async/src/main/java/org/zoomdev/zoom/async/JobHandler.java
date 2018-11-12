package org.zoomdev.zoom.async;

public interface JobHandler<T> {
    void execute(T data);
}
