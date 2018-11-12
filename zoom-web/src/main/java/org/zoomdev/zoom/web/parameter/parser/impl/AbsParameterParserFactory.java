package org.zoomdev.zoom.web.parameter.parser.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.Destroyable;
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
    private static final EmptyParamterParser EMPTY = new EmptyParamterParser();

    public static String[] getPathVariableNames(Method method, String[] names) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Class<?>[] types = method.getParameterTypes();
        int c = types.length;
        Type[] genericTypes = method.getGenericParameterTypes();
        ParameterAdapter<?>[] adapters = new ParameterAdapter<?>[c];
        List<String> pathValiableNames = new ArrayList<String>();
        for (int i = 0; i < c; ++i) {
            Annotation[] annotations = paramAnnotations[i];
            Class<?> type = types[i];
            Type genericType = genericTypes[i];
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    if (param.name().startsWith("{") && param.name().endsWith("}")) {
                        String pathName = param.name()
                                .substring(1, param.name().length() - 1);
                        pathValiableNames.add(pathName);
                        break;
                    } else if (param.pathVariable()) {
                        pathValiableNames.add(StringUtils.isEmpty(param.name()) ? names[i] : param.name());
                        break;
                    }
                }
            }
        }

        return CollectionUtils.toArray(pathValiableNames);
    }

    public AbsParameterParserFactory() {
    }

    protected abstract ParameterAdapter<T> createAdapter(String name, Class<?> type, Type genericType, Annotation[] annotations);

    @SuppressWarnings("rawtypes")
    @Override
    public ParameterParser createParamParser(Class<?> controllerClass, Method method,
                                             String[] names) {


        int c = names.length;
        if (c > 0) {
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            Class<?>[] types = method.getParameterTypes();
            Type[] genericTypes = method.getGenericParameterTypes();
            ParameterAdapter[] adapters = new ParameterAdapter[c];
            for (int i = 0; i < c; ++i) {
                String name = names[i];
                Annotation[] annotations = paramAnnotations[i];
                Class<?> type = types[i];
                Type genericType = genericTypes[i];
                adapters[i] = getAdapter(name, type, genericType, annotations);
            }
            return new DefaultParameterParser(names, types, adapters);

        } else {
            return EMPTY;
        }
    }


    protected ParameterAdapter<?> getAdapter(String name, Class<?> type, Type genericType, Annotation[] annotations) {
        ParameterAdapter<?> adapter = BasicParameterAdapter.getAdapter(type);
        if (adapter != null) {
            return adapter;
        }

        return createAdapter(name, type, genericType, annotations);

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
