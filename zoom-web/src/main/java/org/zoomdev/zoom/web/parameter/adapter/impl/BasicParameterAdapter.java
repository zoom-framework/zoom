package org.zoomdev.zoom.web.parameter.adapter.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BasicParameterAdapter {
    public static final ParameterAdapter<Object> EQ = new EqualAdapter();
    static final ParameterAdapter<Object> SESSION = new SessionAdapter();
    static final ParameterAdapter<Object> REQUEST = new RequestAdapter();
    static final ParameterAdapter<Object> RESPONSE = new ResponseAdapter();
    static final ParameterAdapter<Object> ACTION_CONTEXT = new ActionContextAdapter();

    public static ParameterAdapter<?> getAdapter(Class<?> type) {
        if (HttpServletRequest.class.isAssignableFrom(type)) {
            return REQUEST;
        }
        if (HttpServletResponse.class.isAssignableFrom(type)) {
            return RESPONSE;
        }
        if (ActionContext.class.isAssignableFrom(type)) {
            return ACTION_CONTEXT;
        }
        if (HttpSession.class.isAssignableFrom(type)) {
            return SESSION;
        }

        return null;
    }

    static class EqualAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Class<?> type) {

            return data;
        }

    }

    static class SessionAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Class<?> type) {

            return context.getRequest().getSession();
        }

    }


    static class RequestAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Class<?> type) {

            return context.getRequest();
        }

    }


    static class ResponseAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Class<?> type) {

            return context.getResponse();
        }

    }


    static class ActionContextAdapter implements ParameterAdapter<Object> {

        @Override
        public Object get(ActionContext context, Object data, String name, Class<?> type) {

            return context;
        }

    }

}
