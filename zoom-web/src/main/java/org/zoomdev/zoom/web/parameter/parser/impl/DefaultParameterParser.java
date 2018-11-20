package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterAdapter;

import java.lang.reflect.Type;


class DefaultParameterParser implements ParameterParser {

    private String[] names;
    private Type[] types;
    @SuppressWarnings("rawtypes")
    private ParameterAdapter[] adapters;

    @SuppressWarnings("rawtypes")
    public DefaultParameterParser(
            String[] names,
            Type[] types,
            ParameterAdapter[] adapters) {
        this.names = names;
        this.types = types;
        this.adapters = adapters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] parse(ActionContext context) throws Exception {
        Object data = context.getPreParam();
        final ParameterAdapter[] adapters = this.adapters;
        final String[] names = this.names;
        final Type[] types = this.types;
        int c = names.length;
        Object[] args = new Object[c];
        for (int i = 0; i < c; ++i) {
            args[i] = adapters[i].get(context, data, names[i], types[i]);
        }
        return args;
    }


}