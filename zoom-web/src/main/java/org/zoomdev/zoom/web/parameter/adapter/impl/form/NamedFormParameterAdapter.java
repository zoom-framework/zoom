package org.zoomdev.zoom.web.parameter.adapter.impl.form;

import javax.servlet.http.HttpServletRequest;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

public class NamedFormParameterAdapter implements ParameterAdapter<HttpServletRequest>  {

	public static final ParameterAdapter<HttpServletRequest> ADAPTER = new NamedFormParameterAdapter();

	@Override
	public Object get(ActionContext context, HttpServletRequest data, String name, Class<?> type) {
		return data.getParameter(name);
	}

}
