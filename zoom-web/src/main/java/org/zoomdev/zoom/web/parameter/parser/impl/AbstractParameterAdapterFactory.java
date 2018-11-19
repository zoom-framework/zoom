package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.web.annotations.Param;
import org.zoomdev.zoom.web.parameter.ParameterAdapterFactory;
import org.zoomdev.zoom.web.parameter.ParameterAdapterMaker;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;
import org.zoomdev.zoom.web.parameter.adapter.impl.BasicParameterAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractParameterAdapterFactory<T> implements ParameterAdapterFactory, Destroyable {


    private List<ParameterAdapterMaker> factories = new ArrayList<ParameterAdapterMaker>();


    public AbstractParameterAdapterFactory() {
    }

    protected abstract ParameterAdapter<T> createAdapter(String name, Type type, Annotation[] annotations);

    @Override
    public ParameterAdapter createParameterAdapter(String name, Type type, Annotation[] annotations) {
        ParameterAdapter<?> adapter = BasicParameterAdapter.getAdapter(type);
        if (adapter != null) {
            return adapter;
        }

        //用户自定义的
        for (ParameterAdapterMaker maker : factories) {
            adapter = maker.createParameterAdapter(name, type, annotations);
            if (adapter != null) {
                return adapter;
            }
        }

        return createAdapter(name, type, annotations);
    }


    protected static boolean isPathVariable(String name, Annotation[] annotations) {
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

    @Override
    public void addAdapterMaker(ParameterAdapterMaker factory) {
        factories.add(factory);
    }

    protected static boolean isRequestBody(String name, Annotation[] annotations) {
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
