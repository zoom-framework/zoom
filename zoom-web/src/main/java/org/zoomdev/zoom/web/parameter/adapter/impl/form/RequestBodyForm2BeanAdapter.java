package org.zoomdev.zoom.web.parameter.adapter.impl.form;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestBodyForm2BeanAdapter implements ParameterAdapter<HttpServletRequest> {

    public static final ParameterAdapter<HttpServletRequest> ADAPTER = new RequestBodyForm2BeanAdapter();

    @Override
    public Object get(ActionContext context, HttpServletRequest data, String name, Class<?> type) {

        try {
            Object target = type.newInstance();

            RequestUtils.toBean(context.getRequest(), target);

            return target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
