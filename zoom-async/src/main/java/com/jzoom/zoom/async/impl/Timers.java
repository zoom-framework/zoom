package com.jzoom.zoom.async.impl;

public class Timers {

	
	public static long escape(Runnable runnable) {
		long time = System.currentTimeMillis();
		runnable.run();
		return System.currentTimeMillis()- time;
	}
}
