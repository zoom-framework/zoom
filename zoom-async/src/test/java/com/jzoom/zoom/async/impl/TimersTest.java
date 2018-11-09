package com.jzoom.zoom.async.impl;

import junit.framework.TestCase;

public class TimersTest extends TestCase{

	public void test() {
		System.out.print( Timers.escape(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

				for(int i=0;  i < 1 ; ++i) {
					StackTraceElement[] elements = Thread.currentThread().getStackTrace();
					for (StackTraceElement stackTraceElement : elements) {
						System.out.println(
								stackTraceElement.getClassName() + "#"+stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber());
					}
				}
			}
		})  );
		
		
	}
}
