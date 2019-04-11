package org.zoomdev.zoom.async.impl;

import junit.framework.TestCase;
import org.apache.commons.lang3.mutable.MutableInt;
import org.zoomdev.zoom.async.JobExecutor;
import org.zoomdev.zoom.async.JobHandler;
import org.zoomdev.zoom.async.JobQueue;
import org.zoomdev.zoom.async.JobResult;
import org.zoomdev.zoom.http.utils.Classes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class AsyncServiceTest extends TestCase {


    private static class MyTask {

    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();


    }

    Callable<String> callable = new Callable<String>() {
        @Override
        public String call() throws Exception {
            Thread.sleep(10);
            return "myData";
        }
    };

    public void testNamedHandler() throws InterruptedException, ExecutionException {

        JobQueue jobQueue = Asyncs.defaultJobQueue();
        final FutureTask<String> future = new FutureTask<String>(callable);


        jobQueue.register("myHandler", new JobHandler<MyTask>() {
            @Override
            public void execute(MyTask data) {
                future.run();
            }
        });


        jobQueue.run("myHandler", new MyTask());
        String data = future.get();
        assertEquals(data, "myData");


        jobQueue.unregister("myHandler");

        jobQueue.run("myHandler", new MyTask());

    }


    public void testExecute() throws InterruptedException, ExecutionException {
        final MutableInt mutableInt = new MutableInt(0);
        JobQueue jobQueue = Asyncs.defaultJobQueue();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        jobQueue.run(new Runnable() {
            @Override
            public void run() {
                mutableInt.add(1);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
        assertEquals((int) (Integer) mutableInt.getValue(), 1);

        Future<Object> future = jobQueue.submit("myData", new JobExecutor<String, Object>() {
            @Override
            public Object execute(String data) throws Exception {
                Thread.sleep(10);
                return data;
            }
        });

        assertEquals(future.get(), "myData");
    }


    public void testExecuteList() throws InterruptedException, ExecutionException {
        final MutableInt mutableInt = new MutableInt(0);
        JobQueue jobQueue = Asyncs.defaultJobQueue();

        List<Integer> jobs = Arrays.asList(1, 2, 3, 4, 5, 6);

        final CountDownLatch countDownLatch = new CountDownLatch(jobs.size());

        jobQueue.execute(jobs, new JobExecutor<Integer, Object>() {
            @Override
            public Object execute(Integer data) throws Exception {
                Thread.sleep(1);
                synchronized (mutableInt) {
                    mutableInt.add(1);
                }

                countDownLatch.countDown();
                return null;
            }
        });
        countDownLatch.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(mutableInt.getValue(), (Integer) jobs.size());


    }


    public void testExecuteManyList() throws InterruptedException, ExecutionException {
        final MutableInt mutableInt = new MutableInt(0);
        JobQueue jobQueue = Asyncs.newJobQueue(10);

        List<List<Integer>> jobs = Arrays.asList(
                Arrays.asList(1, 2, 3, 4, 5, 6),
                Arrays.asList(1, 2, 3, 4, 5, 6),
                Arrays.asList(1, 2, 3, 4, 5, 6),
                Arrays.asList(1, 2, 3, 4, 5, 6),
                Arrays.asList(1, 2, 3, 4, 5, 6)
        );
        final CountDownLatch countDownLatch = new CountDownLatch(jobs.size() * 6);


        jobQueue.execute(jobs.iterator(), new JobExecutor<Integer, Object>() {
            @Override
            public Object execute(Integer data) throws Exception {
                Thread.sleep((long) (100 * Math.random()));
                countDownLatch.countDown();
                return data;
            }
        }, new JobResult<List<Object>>() {
            @Override
            public void onResult(List<Object> result) {

                System.out.println(result);
            }
        });


        Classes.destroy(jobQueue);

        assertEquals(countDownLatch.getCount(), 0);

    }
}
