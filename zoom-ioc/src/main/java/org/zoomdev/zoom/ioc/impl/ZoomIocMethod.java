package org.zoomdev.zoom.ioc.impl;

import org.zoomdev.zoom.ioc.IocException;
import org.zoomdev.zoom.ioc.IocKey;
import org.zoomdev.zoom.ioc.IocMethod;
import org.zoomdev.zoom.ioc.IocObject;

import java.lang.reflect.Method;

public class ZoomIocMethod implements IocMethod {
	
	
	private IocKey[] parameterKeys;

	private Method method;


    public ZoomIocMethod(IocKey[] parameterKeys, Method method) {
		assert(parameterKeys!=null && method!=null);
		this.parameterKeys = parameterKeys;
		this.method = method;
	}

	@Override
	public IocKey[] getParameterKeys() {
		return parameterKeys;
	}


    @Override
    public Object invoke(IocObject obj, IocObject[] values) {
		try {
            return method.invoke(obj.get(), ZoomIoc.getValues(values));
		} catch (Exception e) {
			throw new IocException("调用ioc注入函数失败"+method,e);
        }
    }

	@Override
	public Method getMethod() {
		return method;
	}

}
