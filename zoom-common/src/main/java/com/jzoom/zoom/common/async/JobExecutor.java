package com.jzoom.zoom.common.async;

public interface JobExecutor<T,R> {
	R execute(T data);
}
