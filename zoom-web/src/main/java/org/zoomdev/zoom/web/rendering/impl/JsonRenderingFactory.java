package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;

import java.lang.reflect.Method;

public class JsonRenderingFactory implements RenderingFactory {

    private JsonRendering jsonRendering = new JsonRendering();
    private JsonErrorRendering errorRendering = new JsonErrorRendering();


    
    @Override
    public Rendering createRendering(Class<?> targetClass, Method method) {
        if (targetClass.isAnnotationPresent(JsonResponse.class)
                || method.isAnnotationPresent(JsonResponse.class)) {
            return jsonRendering;
        }
        return null;
    }

    @Override
    public Rendering createExceptionRendering(Class<?> targetClass, Method method) {
        if (targetClass.isAnnotationPresent(JsonResponse.class)
                || method.isAnnotationPresent(JsonResponse.class)) {
            return errorRendering;
        }
        return null;
    }
}
