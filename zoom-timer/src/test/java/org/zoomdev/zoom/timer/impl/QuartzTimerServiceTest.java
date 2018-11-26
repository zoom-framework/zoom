package org.zoomdev.zoom.timer.impl;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.zoomdev.zoom.timer.TimerJob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QuartzTimerServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    public static class TestJob implements TimerJob<Object> {

        @Override
        public void execute(Object data) {
            synchronized (data) {
                data.notify();
            }
        }
    }

    @Test
    public void test() throws SchedulerException {
        QuartzTimerService service = new QuartzTimerService();
        Object lock = new Object();
        MutableInt value = new MutableInt(0);
        service.startTimer("test", TestJob.class, lock, "0/1 * * * * ? ");
        synchronized (lock) {
            try {
                lock.wait();
                value.add(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        service.stopTimer("test");
        assertEquals(value.intValue(), 1);
    }


    @Test
    public void test1() throws SchedulerException {
        QuartzTimerService service = new QuartzTimerService();
        Object lock = new Object();
        MutableInt value = new MutableInt(0);
        service.startTimer("test", TestJob.class, lock, "0/1 * * * * ? ");
        synchronized (lock) {
            try {
                lock.wait();
                value.add(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        service.stopAll();
        service.destroy();
        assertEquals(value.intValue(), 1);
        assertNull(service.map);
    }

}
