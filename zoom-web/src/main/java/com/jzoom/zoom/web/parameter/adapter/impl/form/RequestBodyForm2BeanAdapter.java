package com.jzoom.zoom.web.parameter.adapter.impl.form;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.adapter.ParameterAdapter;
import com.jzoom.zoom.web.utils.RequestUtils;

public class RequestBodyForm2BeanAdapter implements ParameterAdapter<HttpServletRequest> {

	public static final ParameterAdapter<HttpServletRequest> ADAPTER = new RequestBodyForm2BeanAdapter();

	@Override
	public Object get(ActionContext context, HttpServletRequest data, String name, Class<?> type) {
		
		try {
			Object target = type.newInstance();
			
			RequestUtils.toBean( context.getRequest(), target );
			
			return target;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
