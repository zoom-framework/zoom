package org.zoomdev.zoom.web.parameter.adapter.impl.form;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

public class RequestBodyForm2MapAdapter implements ParameterAdapter<HttpServletRequest> {

    public static final ParameterAdapter<HttpServletRequest> ADAPTER = new RequestBodyForm2MapAdapter();

    @Override
    public Object get(ActionContext context, HttpServletRequest data, String name, Type type) {
        return Caster.toType(RequestUtils.getParameters(data), type);
    }

}
