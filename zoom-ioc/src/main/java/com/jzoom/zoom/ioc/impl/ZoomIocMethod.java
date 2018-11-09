package com.jzoom.zoom.ioc.impl;

import com.jzoom.zoom.ioc.IocException;
import com.jzoom.zoom.ioc.IocKey;
import com.jzoom.zoom.ioc.IocMethod;
import com.jzoom.zoom.ioc.IocObject;

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
