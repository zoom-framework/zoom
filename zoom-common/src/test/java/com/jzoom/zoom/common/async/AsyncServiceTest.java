package com.jzoom.zoom.common.async;

import junit.framework.TestCase;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncServiceTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	
	public void test() throws InterruptedException, ExecutionException {
		
		AsyncService service = new AsyncService(10);
		
		Future<?> future = service.submit(new Callable() {

			@Override
			public Object call() throws Exception {
				
				return null;
			}
		});
		
		future.get();
		
		
		service.destroy();
		
		
	}

}
