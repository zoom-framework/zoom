package com.jzoom.zoom.dao.modules;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.Module;
import com.jzoom.zoom.dao.transaction.TransMethodInterceptorMaker;

@Module
public class DaoModule {

	@Inject
	public void config(AopFactory factory) {
		factory.methodInterceptorFactory(new TransMethodInterceptorMaker(), 10000);
	}

	
	
}
