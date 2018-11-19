package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.web.action.Action;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterAdapterFactory;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserContainer;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 有几个ParameterAdapterFactory就创建几个
 */
class ParameterParserContainerImpl implements ParameterParserContainer {


    private ParameterParser parameterParser;

    private ParameterAdapterFactory adapterFactory;

    public ParameterParserContainerImpl(ParameterAdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    @Override
    public boolean shouldAdapt(ActionContext context) {
        return adapterFactory.shouldAdapt(context);
    }

    @Override
    public Object[] parse(ActionContext context) throws Exception {
        if (parameterParser == null) {
            synchronized (this) {
                if (parameterParser == null) {
                    Action action = context.getAction();
                    //create
                    parameterParser = createParamParser(action.getMethod(), action.getParameterNames());
                }
            }

        }
        return parameterParser.parse(context);
    }


    @SuppressWarnings("rawtypes")
    public ParameterParser createParamParser(Method method, String[] names) {
        int c = names.length;
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Type[] genericTypes = method.getGenericParameterTypes();
        ParameterAdapter[] adapters = new ParameterAdapter[c];
        for (int i = 0; i < c; ++i) {
            adapters[i] = adapterFactory.createParameterAdapter(names[i], genericTypes[i], paramAnnotations[i]);
        }
        return new DefaultParameterParser(names, genericTypes, adapters);
    }
}
