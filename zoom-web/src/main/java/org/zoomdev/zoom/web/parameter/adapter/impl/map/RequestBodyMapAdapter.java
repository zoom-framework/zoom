package org.zoomdev.zoom.web.parameter.adapter.impl.map;

import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.adapter.ParameterAdapter;

import java.lang.reflect.Field;
import java.util.Map;

public class RequestBodyMapAdapter implements ParameterAdapter<Map<String, Object>> {


    public static final ParameterAdapter<Map<String, Object>> ADAPTER = new RequestBodyMapAdapter();

    public RequestBodyMapAdapter() {
    }

    @Override
    public Object get(ActionContext context, Map<String, Object> data, String name, Class<?> type) {

        try {
            Object bean = type.newInstance();
            Field[] fields = CachedClasses.getFields(type);
            for (Field field : fields) {
                Object value = data.get(field.getName());
                if (value == null) {
                    continue;
                }

                //如果是泛型参数?
                value = Caster.toType(value, field.getGenericType());
                field.set(bean, value);
            }


            return bean;
        } catch (Exception e) {
            throw new RuntimeException(String.format("初始化Bean失败,class:%s 参数:%s", type, data), e);
        }

    }

}
