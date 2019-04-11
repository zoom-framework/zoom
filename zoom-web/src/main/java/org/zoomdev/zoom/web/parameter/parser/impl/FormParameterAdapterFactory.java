package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.http.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

class FormParameterAdapterFactory extends AbstractParameterAdapterFactory<HttpServletRequest> {
    public static final ParameterAdapter<HttpServletRequest> ADAPTER = new NamedFormParameterAdapter();

    public static class NamedFormParameterAdapter implements ParameterAdapter<HttpServletRequest> {


        /**
         * 这里得看看Array的情形
         *
         * @param context context
         * @param data    数据 (Map或者 HttpServletRequest)
         * @param name    参数名称
         * @param type    参数类型
         * @return
         */
        @Override
        public Object get(ActionContext context, HttpServletRequest data, String name, Type type) {
            return Caster.toType(data.getParameter(name), type);
        }

    }


    @Override
    public void destroy() {

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected ParameterAdapter<HttpServletRequest> createAdapter(
            String name,
            Type type,
            Annotation[] annotations) {
        return ADAPTER;

    }


    @Override
    public boolean shouldAdapt(ActionContext context) {
        return context.getPreParam() instanceof HttpServletRequest;
    }
}
