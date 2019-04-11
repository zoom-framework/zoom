package org.zoomdev.zoom.async.modules;

import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.async.JobQueue;
import org.zoomdev.zoom.async.aop.AsyncMethodAopMaker;
import org.zoomdev.zoom.async.impl.Asyncs;
import org.zoomdev.zoom.http.annotations.Inject;
import org.zoomdev.zoom.http.annotations.IocBean;
import org.zoomdev.zoom.http.annotations.Module;

@Module
public class AsyncModule {

    @Inject
    public void config(AopFactory factory) {
        factory.addMethodInterceptorFactory(new AsyncMethodAopMaker(), 0);
    }

    @IocBean
    public JobQueue getJobQueue() {
        return Asyncs.defaultJobQueue();
    }

}
