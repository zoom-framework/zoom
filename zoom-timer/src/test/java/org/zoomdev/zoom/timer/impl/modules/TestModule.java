package org.zoomdev.zoom.timer.impl.modules;

import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.timer.annotation.Timer;

@Module
public class TestModule {


    public static  class TestService{

        @Timer(every = Timer.Every.SECOND,everyValue = 1)
        public void test(){

        }
    }

    @IocBean
    public TestService getTestService(){
        return new TestService();
    }

}
