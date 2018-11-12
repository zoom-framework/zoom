package org.zoomdev.zoom.web.parameter.adapter.impl.form;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestBodyForm2MapAdapter implements ParameterAdapter<HttpServletRequest> {

    public static final ParameterAdapter<HttpServletRequest> ADAPTER = new RequestBodyForm2MapAdapter();

    @Override
    public Object get(ActionContext context, HttpServletRequest data, String name, Class<?> type) {
        return RequestUtils.getParameters(data);
    }

}
