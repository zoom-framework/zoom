package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Field;

public class ZoomBeanIocField extends  ZoomIocField implements IocField{
    protected IocKey key;

    public ZoomBeanIocField(IocContainer ioc,Field field,IocKey key) {
        super(ioc,field);
        this.key = key;
    }


    @Override
	public void set(IocObject obj) {
		try {
			field.set(obj.get(), ioc.get(key).get());
		} catch (Exception e) {
			throw new IocException("设置字段值失败",e);
        }
	}




}
