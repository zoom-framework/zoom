package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.WebConfig;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;

import java.lang.reflect.Method;

public class TemplateRenderingFactory implements RenderingFactory {

    private final TemplateEngineManager templateEngineManager;
    private final WebConfig webConfig;

    public TemplateRenderingFactory(
            TemplateEngineManager templateEngineManage,
            WebConfig webConfig){
        assert (templateEngineManage!=null && webConfig!=null);
        this.templateEngineManager = templateEngineManage;
        this.webConfig = webConfig;
    }

    @Override
    public Rendering createRendering(Class<?> targetClass, Method method) {
        return templateEngineManager.getEngine(webConfig.getTemplateExt());
    }

    @Override
    public Rendering createExceptionRendering(Class<?> targetClass, Method method) {
        return templateEngineManager.getEngine(webConfig.getTemplateExt());
    }
}
