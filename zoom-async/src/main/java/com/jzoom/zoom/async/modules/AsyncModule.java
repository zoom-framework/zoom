package com.jzoom.zoom.async.modules;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.async.aop.AsyncMethodAopMaker;
import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.IocBean;
import com.jzoom.zoom.common.annotations.Module;
import com.jzoom.zoom.common.async.Asyncs;
import com.jzoom.zoom.common.async.JobQueue;

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
