package org.zoomdev.zoom.ioc.modules;

import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.ioc.models.PushService;
import org.zoomdev.zoom.ioc.models.impl.PushServiceImpl;

import java.util.concurrent.CountDownLatch;

@Module
public class ShopModule {



    @IocBean
    public PushService getPushService(){

        return new PushServiceImpl("testKey");
    }


    @IocBean
    public CountDownLatch getCountDownLatch(){
        return new CountDownLatch(100);
    }
}
