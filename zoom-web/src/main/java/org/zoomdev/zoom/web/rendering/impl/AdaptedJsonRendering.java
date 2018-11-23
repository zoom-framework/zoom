package org.zoomdev.zoom.web.rendering.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.resp.JsonResponseAdapter;
import org.zoomdev.zoom.web.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AdaptedJsonRendering implements Rendering {

    private final JsonResponseAdapter adapter;

    public AdaptedJsonRendering(JsonResponseAdapter adapter) {
        assert (adapter!=null);
        this.adapter = adapter;
    }

    @Override
    public boolean render(ActionContext context) throws Exception {

        HttpServletResponse response = context.getResponse();

        Object result = context.getResult();
        Throwable exception = context.getException();
        if(exception!=null){
            result = adapter.adapterException(exception);
        }else{
            result = adapter.adapterOk(result);
        }
        ResponseUtils.json(response, result);
        return true;

    }

    @Override
    public boolean shouldHandle(Class<?> targetClass, Method method) {
        JsonResponse response = targetClass.getAnnotation(JsonResponse.class);
        if(response==null){
            response = method.getAnnotation(JsonResponse.class);
        }

        return response!=null && response.value() != null;
    }

    @Override
    public String getUid() {
        return adapter.getClass().getName();
    }


}
