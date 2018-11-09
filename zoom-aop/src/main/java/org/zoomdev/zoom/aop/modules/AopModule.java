package org.zoomdev.zoom.aop.modules;

import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactory;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.aop.javassist.JavassistClassInfo;
import org.zoomdev.zoom.ioc.IocContainer;

@Module()
public class AopModule {


    @IocBean
    public AopFactory getAopFactory(){
        return new JavassistAopFactory();
    }


    @IocBean
    public ClassInfo getClassInfo(){
        return new JavassistClassInfo();
    }


    @Inject
    public void config(IocContainer ioc,AopFactory aopFactory){
        ioc.setClassEnhancer(aopFactory);
    }

}
