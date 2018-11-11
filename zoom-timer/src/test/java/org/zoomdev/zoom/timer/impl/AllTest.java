package org.zoomdev.zoom.timer.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        QuartzTimerServiceTest.class,
        TestTimerModule.class
})
public class AllTest {
}
