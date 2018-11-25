package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ZoomIocMethod extends IocBase implements IocMethod {


    private IocKey[] parameterKeys;

    private final Method method;

    // 唯一id，在ioc容器中的
    private String uid;


    private IocClass iocClass;


    public ZoomIocMethod(IocContainer ioc, IocClass iocClass, IocKey[] parameterKeys, Method method) {
        super(ioc);
        assert (parameterKeys != null && method != null && iocClass != null);
        this.parameterKeys = parameterKeys;
        this.iocClass = iocClass;
        this.method = method;
    }


    private String getKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(iocClass.getKey().toString())
                .append(method.getName());

        for (IocKey key : parameterKeys) {
            sb.append(key.toString());
        }

        return sb.toString();

    }

    /**
     * @return
     */
    @Override
    public String getUid() {

        return uid == null ? (
                uid = getKey()
        ) : uid;
    }

    @Override
    public IocKey[] getParameterKeys() {
        return parameterKeys;
    }


    @Override
    public Object invoke(IocObject obj) {
        try {
            IocObject[] values = ioc.fetchValues(parameterKeys);
            return method.invoke(obj.get(), ZoomIocContainer.getValues(values));
        } catch (Exception e) {
            throw new IocException("调用ioc注入函数失败" + method, e);
        }
    }


    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }


    @Override
    public void inject(IocObject target) {
        invoke(target);
    }
}
