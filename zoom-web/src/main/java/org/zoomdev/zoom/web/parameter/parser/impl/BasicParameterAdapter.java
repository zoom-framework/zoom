package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.annotations.Param;
import org.zoomdev.zoom.web.parameter.ParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

class BasicParameterAdapter {
    static final ParameterAdapter<Object> SESSION = new SessionAdapter();
    static final ParameterAdapter<Object> REQUEST = new RequestAdapter();
    static final ParameterAdapter<Object> RESPONSE = new ResponseAdapter();
    static final ParameterAdapter<Object> ACTION_CONTEXT = new ActionContextAdapter();



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

    public static ParameterAdapter<?> getAdapter(String name, Type type, Annotation[] annotations) {
        if (type instanceof Class) {
            Class<?> classOfParameter = (Class<?>) type;
            if (HttpServletRequest.class.isAssignableFrom(classOfParameter)) {
                return REQUEST;
            }
            if (HttpServletResponse.class.isAssignableFrom(classOfParameter)) {
                return RESPONSE;
            }
            if (ActionContext.class.isAssignableFrom(classOfParameter)) {
                return ACTION_CONTEXT;
            }
            if (HttpSession.class.isAssignableFrom(classOfParameter)) {
                return SESSION;
            }

        }

        if (isRequestBody(name, annotations)) {
            return RequestBodyAdapter.ADAPTER;
        } else if (isPathVariable(name, annotations)) {
            //简单类型直接来
            return PathParameterAdapter.ADAPTER;
        }

        return null;
    }


    public static class PathParameterAdapter implements ParameterAdapter<Object> {

        public static final PathParameterAdapter ADAPTER = new PathParameterAdapter();

        public PathParameterAdapter() {

        }

        @Override
        public Object get(ActionContext context, Object data, String name, Type type) {
            return Caster.toType(context.getRequest().getAttribute(name),type);
        }

    }



    static class SessionAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Type type) {

            return context.getRequest().getSession();
        }

    }


    static class RequestAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Type type) {

            return context.getRequest();
        }

    }


    static class ResponseAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Type type) {

            return context.getResponse();
        }

    }


    static class ActionContextAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Type type) {

            return context;
        }

    }

    public static class RequestBodyAdapter implements ParameterAdapter<Object> {


        public static final ParameterAdapter<Object> ADAPTER = new RequestBodyAdapter();

        public RequestBodyAdapter() {

        }

        @Override
        public Object get(ActionContext context, Object data, String name, Type type) {
            return Caster.toType(data, type);
        }

    }


}
