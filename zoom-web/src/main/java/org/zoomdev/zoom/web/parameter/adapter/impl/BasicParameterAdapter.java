package org.zoomdev.zoom.web.parameter.adapter.impl;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Type;

public class BasicParameterAdapter {
    public static final ParameterAdapter<Object> EQ = new EqualAdapter();
    static final ParameterAdapter<Object> SESSION = new SessionAdapter();
    static final ParameterAdapter<Object> REQUEST = new RequestAdapter();
    static final ParameterAdapter<Object> RESPONSE = new ResponseAdapter();
    static final ParameterAdapter<Object> ACTION_CONTEXT = new ActionContextAdapter();

    public static ParameterAdapter<?> getAdapter(Type type) {
        if(type instanceof Class){
            Class<?> classOfParameter = (Class<?>)type;
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
        return null;
    }

    static class EqualAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Type type) {

            return data;
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

}
