package org.zoomdev.zoom.aop.modules;

import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistClassInfo;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.http.annotations.Inject;
import org.zoomdev.zoom.http.annotations.IocBean;
import org.zoomdev.zoom.http.annotations.Module;
import org.zoomdev.zoom.ioc.IocContainer;

@Module()
public class AopModule {


    @IocBean(order = IocBean.SYSTEM)
    public AopFactory getAopFactory() {
        return new JavassistAopFactory();
    }


    @IocBean
    public ClassInfo getClassInfo() {
        return new JavassistClassInfo();
    }


    @Inject
    public void config(IocContainer ioc, AopFactory aopFactory) {
        ioc.setClassEnhancer(aopFactory);
    }

}
