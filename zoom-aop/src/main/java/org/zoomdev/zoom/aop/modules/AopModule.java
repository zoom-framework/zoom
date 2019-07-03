package org.zoomdev.zoom.aop.modules;

import javassist.ClassPool;
import org.zoomdev.zoom.aop.AopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistAopFactory;
import org.zoomdev.zoom.aop.javassist.JavassistClassInfo;
import org.zoomdev.zoom.aop.reflect.ClassInfo;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.ioc.IocContainer;

@Module()
public class AopModule {


    @IocBean
    public ClassPool getClassPool() {
        return new ClassPool(ClassPool.getDefault());
    }

    @IocBean(order = IocBean.SYSTEM)
    public AopFactory getAopFactory(ClassPool classPool) {
        return new JavassistAopFactory(classPool);
    }


    @IocBean
    public ClassInfo getClassInfo(ClassPool classPool) {
        return new JavassistClassInfo(classPool);
    }


    @Inject
    public void config(IocContainer ioc, AopFactory aopFactory) {
        ioc.setClassEnhancer(aopFactory);
    }

}
