package org.zoomdev.zoom.web.parameter.adapter.impl.map;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import java.lang.reflect.Type;
import java.util.Map;


public class Map2BeanAdapter implements ParameterAdapter<Map<String, Object>> {


    public Map2BeanAdapter() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(ActionContext context, Map<String, Object> data, String name, Type type) {

        return Caster.toType(data.get(name), type);

    }

}