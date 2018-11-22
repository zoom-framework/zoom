package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.WebConfig;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;

public class SimpleTemplateEngineManager implements TemplateEngineManager {

    private WebConfig webConfig;

    public SimpleTemplateEngineManager(WebConfig webConfig){
        this.webConfig = webConfig;
    }

    @Override
    public TemplateRendering getEngine(String name) {
        return null;
    }

    @Override
    public void register(String name, TemplateRendering rendering) {

    }

    @Override
    public int getEngineCont() {
        return 0;
    }
}
