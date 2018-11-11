package org.zoomdev.zoom.async.impl;

import junit.framework.TestCase;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncsTest extends TestCase {

    public void testDelay() throws ExecutionException, InterruptedException {
        Future<String> future = Asyncs.delay(1,"hello");
        String str = future.get();
        assertEquals(str,"hello");
    }

    public void testEscape() {
        System.out.print( Timers.escape(new Runnable() {

            @Override
            public void run() {
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
