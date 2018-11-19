package org.zoomdev.zoom.web.parameter.parser.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.web.annotations.Param;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.BasicParameterAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsParameterParserFactory<T> implements ParameterParserFactory, Destroyable {




    public AbsParameterParserFactory() {
    }

    protected abstract ParameterAdapter<T> createAdapter(String name, Type type, Annotation[] annotations);

    @SuppressWarnings("rawtypes")
    @Override
    public ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names) {
        int c = names.length;
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Type[] genericTypes = method.getGenericParameterTypes();
        ParameterAdapter[] adapters = new ParameterAdapter[c];
        for (int i = 0; i < c; ++i) {
            adapters[i] = getAdapter(names[i], genericTypes[i], paramAnnotations[i]);
        }
        return new DefaultParameterParser(names, genericTypes, adapters);
    }


    protected ParameterAdapter<?> getAdapter(String name, Type type, Annotation[] annotations) {
        ParameterAdapter<?> adapter = BasicParameterAdapter.getAdapter(type);
        if (adapter != null) {
            return adapter;
        }

        return createAdapter(name, type, annotations);

    }


    protected boolean isPathVariable(String name, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                Param param = (Param) annotation;
                if (param.name().startsWith("{") && param.name().endsWith("}")) {
                    return true;
                }
                if (param.pathVariable()) {
                    return true;
                }
            }
        }
        return false;
    }


    protected boolean isRequestBody(String name, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                Param param = (Param) annotation;
                if (Param.BODY.equals(param.name())) {
                    return true;
                }
                if (param.body()) {
                    return true;
                }
            }
        }
        return false;
    }

}
