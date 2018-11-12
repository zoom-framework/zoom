package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;


public class DefaultParameterParser implements ParameterParser {

    private String[] names;
    private Class<?>[] types;
    @SuppressWarnings("rawtypes")
    private ParameterAdapter[] adapters;

    @SuppressWarnings("rawtypes")
    public DefaultParameterParser(String[] names, Class<?>[] types, ParameterAdapter[] adapters) {
        this.names = names;
        for (String name : names) {
            if (name == null) {
                throw new RuntimeException("name不能为null");
            }
        }
        this.types = types;
        this.adapters = adapters;
        for (ParameterAdapter parameterAdapter : adapters) {
            if (parameterAdapter == null) {
                throw new RuntimeException("adapter不能为null");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] parse(ActionContext context) throws Exception {
        Object data = context.getPreParam();
        int c = names.length;
        Object[] args = new Object[c];
        for (int i = 0; i < c; ++i) {
            args[i] = adapters[i].get(context, data, names[i], types[i]);

        }

        return args;
    }


}