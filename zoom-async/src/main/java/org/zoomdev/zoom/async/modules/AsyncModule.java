package org.zoomdev.zoom.async.modules;

import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.async.aop.AsyncMethodAopMaker;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.async.Asyncs;
import org.zoomdev.zoom.common.async.JobQueue;

@Module
public class AsyncModule {

	@Inject
	public void config( AopFactory factory) {
		factory.methodInterceptorFactory(new AsyncMethodAopMaker(), 0);
	}

	@IocBean
	public JobQueue getJobQueue(){
	    return Asyncs.defaultJobQueue();
    }
	
}
