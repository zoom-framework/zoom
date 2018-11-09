package com.jzoom.zoom.timer;

public interface TimerJob<T> {
	void execute( T data  );
}
