package org.zoomdev.zoom.dao.transaction;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.MethodInvoker;
import org.zoomdev.zoom.dao.impl.ZoomDao;

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
        } catch (Throwable e) {
            ZoomDao.rollbackTrans();
            throw e;
        }
    }


}
