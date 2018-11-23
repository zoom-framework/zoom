package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class JsonRendering implements Rendering {


    @Override
    public boolean render(ActionContext context) throws Exception {
        Object result = context.getRenderObject();
        HttpServletResponse response = context.getResponse();
        ResponseUtils.json(response, result);
        return true;

    }

    @Override
    public String getUid() {
        return "json";
    }

    @Override
    public boolean shouldHandle(Class<?> targetClass, Method method) {
        return targetClass.isAnnotationPresent(JsonResponse.class) || method.isAnnotationPresent(JsonResponse.class);
    }

}
