package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.http.annotations.IocBean;
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

    int order = -1;

    @Override
    public int getOrder() {
        //如果没有参数，那么就直接执行
        if(order == -1){
            if(parameterKeys==null || parameterKeys.length==0){
                order = IocBean.MAX;
            }else{
                //评估下顺序，往后面推
                int order = 0;
                for(IocKey key : parameterKeys){
                    IocClass iocClass = ioc.getIocClassLoader().get(key);
                    if(iocClass==null){
                        throw new IocException("未找到指定的IocClass:"+key);
                    }
                    order += iocClass.getOrder();
                }
                //最后取一个平均值
                this.order = order / parameterKeys.length;
            }

        }
        return order;
    }
}
