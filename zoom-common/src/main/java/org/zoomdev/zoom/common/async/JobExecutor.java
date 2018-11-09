package org.zoomdev.zoom.common.async;

public interface JobExecutor<T,R> {
	R execute(T data);
}
