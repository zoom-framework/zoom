package org.zoomdev.zoom.ioc.modules;

import jdk.nashorn.internal.lookup.MethodHandleFactory;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.ioc.IocMethod;
import org.zoomdev.zoom.ioc.IocMethodVisitor;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.ioc.annotations.TestListener;
import org.zoomdev.zoom.ioc.impl.AnnotationMethodHandler;
import org.zoomdev.zoom.ioc.models.PushService;
import org.zoomdev.zoom.ioc.models.ShopService;
import org.zoomdev.zoom.ioc.models.impl.PushServiceImpl;

import java.util.concurrent.CountDownLatch;

@Module
public class ShopModule {



    @IocBean(initialize = "init",destroy = "destroy")
    public PushService getPushService(){

        return new PushServiceImpl("testKey");
    }

    @Inject
    public void inject(IocMethodVisitor methodVisitor, final ShopService shopService){
        methodVisitor.add(new AnnotationMethodHandler<TestListener>() {
            @Override
            protected void visit(IocObject target, TestListener annotation, IocMethod method) {

                shopService.setListener(
                    target,
                        method
                );

            }
        });
    }

    @IocBean
    public CountDownLatch getCountDownLatch(){
        return new CountDownLatch(100);
    }
}
