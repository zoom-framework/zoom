package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.BasicParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.NamedMapParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.PathMapParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.RequestBodyMapAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class MapParameterParserFactory extends AbsParameterParserFactory<Map<String, Object>> {


    public MapParameterParserFactory() {

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

}
