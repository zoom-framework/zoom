package com.jzoom.zoom.aop.modules;

import com.jzoom.zoom.aop.AopFactory;
import com.jzoom.zoom.aop.annotations.AopEnable;
import com.jzoom.zoom.aop.javassist.JavassistAopFactory;
import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.IocBean;
import com.jzoom.zoom.common.annotations.Module;
import com.jzoom.zoom.aop.reflect.ClassInfo;
import com.jzoom.zoom.aop.javassist.JavassistClassInfo;
import com.jzoom.zoom.ioc.IocContainer;

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
