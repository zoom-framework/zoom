package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.NamedMapParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.PathMapParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.RequestBodyMapAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

class MapParameterAdapterFactory extends AbstractParameterAdapterFactory<Map<String, Object>> {


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
        if (isRequestBody(name, annotations)) {
            return RequestBodyMapAdapter.ADAPTER;
        } else if (isPathVariable(name, annotations)) {
            //简单类型直接来
            return (ParameterAdapter) PathMapParameterAdapter.ADAPTER;
        } else {
            //简单类型直接来
            return NamedMapParameterAdapter.ADAPTER;
        }
    }

    @Override
    public boolean shouldAdapt(ActionContext context) {
        return context.getPreParam() instanceof Map;
    }


}
