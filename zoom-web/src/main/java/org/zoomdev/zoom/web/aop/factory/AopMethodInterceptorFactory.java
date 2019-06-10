package org.zoomdev.zoom.web.aop.factory;

import org.zoomdev.zoom.aop.MethodInterceptor;
import org.zoomdev.zoom.aop.annotations.Aop;
import org.zoomdev.zoom.aop.factory.AnnotationMethodInterceptorFactory;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.ioc.IocContainer;

import java.lang.reflect.Method;
import java.util.List;

public class AopMethodInterceptorFactory extends AnnotationMethodInterceptorFactory<Aop> {

    private IocContainer ioc;

    public AopMethodInterceptorFactory(IocContainer ioc){
        this.ioc = ioc;
    }

    @Override
    protected void createMethodInterceptors(Aop annotation, Method method, List<MethodInterceptor> interceptors) {

        Class<? extends MethodInterceptor>[] classOfMethodInterceptor = annotation.value();

        for (Class<? extends MethodInterceptor> clazz : classOfMethodInterceptor) {
            try {
                interceptors.add(ioc.fetch(clazz));
            } catch (Exception e) {
                throw new ZoomException(String.format("在初始化MethodInterceptor[%s]的时候发生异常", clazz), e);
            }
        }
    }

}
