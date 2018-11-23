package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.WebConfig;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTemplateEngineManager implements TemplateEngineManager {


    private Map<String,TemplateRendering> pool = new ConcurrentHashMap<String, TemplateRendering>();

    public SimpleTemplateEngineManager(){

    }

    @Override
    public TemplateRendering getEngine(String name) {
        if(!name.startsWith(".")){
            name = "." + name;
        }
        return pool.get(name);
    }

    @Override
    public void register(String name, TemplateRendering rendering) {
        if(!name.startsWith(".")){
            name = "." + name;
        }
        pool.put(name,rendering);
    }

    @Override
    public int getEngineCont() {
        return pool.size();
    }
}
