package org.zoomdev.zoom.async.impl;

import com.sun.xml.internal.ws.util.CompletedFuture;
import org.zoomdev.zoom.async.annotation.Async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public class TestAsyncService {

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    private CountDownLatch countDownLatch;




    @Async
    public void testAsync() throws InterruptedException {

        Thread.sleep(100);

        System.out.println("yes");
        countDownLatch.countDown();

    }

    @Async
    public Future<Integer> testAsyncWithFuture() throws InterruptedException {

        Thread.sleep(100);

        System.out.println("yes");
        countDownLatch.countDown();

        return new CompletedFuture<Integer>(100,null);

    }
}
