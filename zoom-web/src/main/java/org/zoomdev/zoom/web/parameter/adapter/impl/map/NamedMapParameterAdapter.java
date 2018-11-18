package org.zoomdev.zoom.web.parameter.adapter.impl.map;

import javassist.expr.Cast;
import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import java.lang.reflect.Type;
import java.util.Map;


public class NamedMapParameterAdapter implements ParameterAdapter<Map<String, Object>> {

    public static final NamedMapParameterAdapter ADAPTER = new NamedMapParameterAdapter();

    public NamedMapParameterAdapter() {

    }

    @Override
    public Object get(ActionContext context, Map<String, Object> data, String name, Type type) {
        return Caster.toType(data.get(name),type);
    }

}