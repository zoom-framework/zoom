package com.jzoom.zoom.dao.transaction;

import com.jzoom.zoom.aop.MethodInterceptor;
import com.jzoom.zoom.aop.MethodInvoker;
import com.jzoom.zoom.dao.impl.ZoomDao;

public class TransMethodInterceptor implements MethodInterceptor {
	

	
	public TransMethodInterceptor(int level) {
		this.level = level;
	}
	
	private int level;

	
	
	@Override
	public void intercept(MethodInvoker invoker) throws Throwable {
		
		try {
			ZoomDao.beginTrans(level);
			invoker.invoke();
			ZoomDao.commitTrans();
		}catch (Throwable e) {
			ZoomDao.rollbackTrans();
			throw e;
		}
	}
	


}
