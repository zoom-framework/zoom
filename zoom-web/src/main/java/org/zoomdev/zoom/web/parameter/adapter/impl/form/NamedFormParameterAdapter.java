package org.zoomdev.zoom.web.parameter.adapter.impl.form;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

public class NamedFormParameterAdapter implements ParameterAdapter<HttpServletRequest> {

    public static final ParameterAdapter<HttpServletRequest> ADAPTER = new NamedFormParameterAdapter();

    /**
     * 这里得看看Array的情形
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
