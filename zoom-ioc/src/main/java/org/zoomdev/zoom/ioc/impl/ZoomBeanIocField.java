package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.*;
import org.zoomdev.zoom.ioc.*;

import java.lang.reflect.Field;

public class ZoomBeanIocField extends ZoomIocField {

    private IocContainer iocContainer;

	public ZoomBeanIocField(IocKey key, Field field, IocValue value) {
		super(key, field,value);
	}

	@Override
	public void set(IocObject obj, IocObject value) {
		try {
			field.set(obj.get(), value.get());
		} catch (Exception e) {
			throw new IocException("设置字段值失败",e);
        }
	}


    @Override
    public void inject(IocObject target) {

    }
}
