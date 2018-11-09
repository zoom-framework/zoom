package com.jzoom.zoom.common.async;

public interface JobHandler<T> {
	void execute(T data);
}
