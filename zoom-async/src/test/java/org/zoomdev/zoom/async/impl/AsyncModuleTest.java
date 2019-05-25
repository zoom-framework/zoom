package org.zoomdev.zoom.async.impl;

import javassist.ClassPool;
import junit.framework.TestCase;
import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactory;
import org.zoomdev.zoom.async.JobQueue;
import org.zoomdev.zoom.async.modules.AsyncModule;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncModuleTest extends TestCase {

    public void test() throws IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {
        AsyncModule module = new AsyncModule();
        AopFactory factory = new JavassistAopFactory(new ClassPool(ClassPool.getDefault()));
        module.config(factory);

        JobQueue jobQueue = module.getJobQueue();

        assertEquals(jobQueue, Asyncs.defaultJobQueue());

        Class<?> type = factory.enhance(TestAsyncService.class);

        TestAsyncService service = (TestAsyncService) type.newInstance();

        CountDownLatch countDownLatch = new CountDownLatch(2);
        service.setCountDownLatch(countDownLatch);
        service.testAsync();


        Future future = service.testAsyncWithFuture();

        countDownLatch.await();

        assertEquals(future.get(), (Integer) 100);

    }
}
