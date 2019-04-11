package org.zoomdev.zoom.dao.modules;

import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.dao.transaction.TransMethodInterceptorMaker;

@Module
public class DaoModule {

    @Inject
    public void config(AopFactory factory) {
        factory.addMethodInterceptorFactory(new TransMethodInterceptorMaker(), 10000);
    }


}
