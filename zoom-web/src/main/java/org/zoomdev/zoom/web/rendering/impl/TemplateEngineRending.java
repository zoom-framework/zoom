package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;

public class TemplateEngineRending implements Rendering {

    private TemplateEngineManager templateEngineManager;

    public TemplateEngineRending(TemplateEngineManager templateEngineManager){
        this.templateEngineManager = templateEngineManager;

    }

    @Override
    public boolean render(ActionContext context) throws Exception {
        Object data = context.getRenderObject();


        return false;
    }
}
