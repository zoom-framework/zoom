package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.NamedFormParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.RequestBodyForm2BeanAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.form.RequestBodyForm2MapAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.map.PathMapParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class FormParameterParserFactory extends AbsParameterParserFactory<HttpServletRequest> {


    @Override
    public void destroy() {

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected ParameterAdapter<HttpServletRequest> createAdapter(
            String name,
            Type type,
            Annotation[] annotations) {
        if (isRequestBody(name, annotations)) {
            return RequestBodyForm2BeanAdapter.ADAPTER;
        } else if (isPathVariable(name, annotations)) {
            return (ParameterAdapter) PathMapParameterAdapter.ADAPTER;
        } else {
            return NamedFormParameterAdapter.ADAPTER;
        }

    }


}
