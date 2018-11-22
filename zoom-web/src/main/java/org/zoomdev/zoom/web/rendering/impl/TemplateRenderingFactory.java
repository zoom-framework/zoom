package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;
import org.zoomdev.zoom.web.rendering.RenderingFactoryManager;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;

import java.lang.reflect.Method;

public class TemplateRenderingFactory implements RenderingFactory {

    private TemplateEngineManager templateEngineManager;

    public TemplateRenderingFactory(TemplateEngineManager templateEngineManage){
        this.templateEngineManager = templateEngineManage;
    }

    @Override
    public Rendering createRendering(Class<?> targetClass, Method method) {
        return null;
    }
}
