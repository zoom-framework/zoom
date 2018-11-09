package org.zoomdev.zoom.common.async;

public interface JobHandler<T> {
	void execute(T data);
}
