package org.zoomdev.zoom.web.parameter.adapter.impl.map;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import java.lang.reflect.Type;

public class PathMapParameterAdapter implements ParameterAdapter<Object> {

    public static final PathMapParameterAdapter ADAPTER = new PathMapParameterAdapter();

    public PathMapParameterAdapter() {

    }

    @Override
    public Object get(ActionContext context, Object data, String name, Type type) {
        return context.getRequest().getAttribute(name);
    }

}