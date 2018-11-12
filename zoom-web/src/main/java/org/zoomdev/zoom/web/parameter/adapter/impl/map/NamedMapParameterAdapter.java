package org.zoomdev.zoom.web.parameter.adapter.impl.map;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import java.util.Map;


public class NamedMapParameterAdapter implements ParameterAdapter<Map<String, Object>> {

    public static final NamedMapParameterAdapter ADAPTER = new NamedMapParameterAdapter();

    public NamedMapParameterAdapter() {

    }

    @Override
    public Object get(ActionContext context, Map<String, Object> data, String name, Class<?> type) {
        return data.get(name);
    }

}