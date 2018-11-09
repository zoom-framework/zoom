package com.jzoom.zoom.common.utils;

import com.jzoom.zoom.common.queue.ServiceThread;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ProcessUtilTest extends TestCase {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws IOException, InterruptedException {


        final ProcessUtils.ProcessController controller = ProcessUtils.exec("top");

        assertTrue(true);


        ServiceThread thread = new ServiceThread() {
            @Override
            protected boolean repetitionRun() {
                try {
                    controller.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };

        thread.start();

        Thread.sleep(1);
        controller.destroy();
        thread.stop();

    }
}
