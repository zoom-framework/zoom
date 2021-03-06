package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.web.parameter.ParameterAdapter;
import org.zoomdev.zoom.web.parameter.ParameterAdapterFactory;
import org.zoomdev.zoom.web.parameter.ParameterAdapterMaker;

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
        ParameterAdapter<?> adapter = BasicParameterAdapter.getAdapter(name, type, annotations);
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


    @Override
    public void addAdapterMaker(ParameterAdapterMaker factory) {
        factories.add(factory);
    }


}
