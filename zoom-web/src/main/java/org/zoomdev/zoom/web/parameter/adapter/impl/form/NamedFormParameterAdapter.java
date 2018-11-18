package org.zoomdev.zoom.web.parameter.adapter.impl.form;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

public class NamedFormParameterAdapter implements ParameterAdapter<HttpServletRequest> {

    public static final ParameterAdapter<HttpServletRequest> ADAPTER = new NamedFormParameterAdapter();

    @Override
    public Object get(ActionContext context, HttpServletRequest data, String name, Type type) {
        return Caster.toType(data.getParameter(name),type);
    }

}
