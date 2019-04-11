package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.http.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

class MapParameterAdapterFactory extends AbstractParameterAdapterFactory<Map<String, Object>> {
    public static final NamedMapParameterAdapter ADAPTER = new NamedMapParameterAdapter();


    public static class NamedMapParameterAdapter implements ParameterAdapter<Map<String, Object>> {


        public NamedMapParameterAdapter() {

        }

        @Override
        public Object get(ActionContext context, Map<String, Object> data, String name, Type type) {
            return Caster.toType(data.get(name), type);
        }

    }

    public MapParameterAdapterFactory() {

    }

    @Override
    public void destroy() {

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ParameterAdapter<Map<String, Object>> createAdapter(
            String name,
            Type type,
            Annotation[] annotations) {
        return ADAPTER;
    }

    @Override
    public boolean shouldAdapt(ActionContext context) {
        return context.getPreParam() instanceof Map;
    }


}
