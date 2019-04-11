package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Field;

public class ZoomBeanIocField extends ZoomIocField implements IocField {
    protected IocKey key;

    public ZoomBeanIocField(IocContainer ioc, Field field, IocKey key) {
        super(ioc, field);
        this.key = key;
    }


    @Override
    public void set(IocObject obj) {
        try {
            field.set(obj.get(), ioc.fetch(key).get());
        } catch (Exception e) {
            throw new IocException("设置字段值失败", e);
        }
    }

    private int order=-1 ;

    ///根据Type的Order
    @Override
    public int getOrder() {
        if(order==-1){
            IocClass iocClass = ioc.getIocClassLoader().get(key);
            if(iocClass==null){
                throw new IocException("未找到指定的IocClass:"+key);
            }
            order = iocClass.getOrder() - 10;
        }
        return order;
    }
}
