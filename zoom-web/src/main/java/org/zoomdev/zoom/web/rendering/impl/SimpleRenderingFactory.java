package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingChain;
import org.zoomdev.zoom.web.rendering.RenderingFactory;

import java.awt.image.renderable.RenderContext;
import java.lang.reflect.Method;
import java.util.*;

public class SimpleRenderingFactory implements RenderingFactory{


    List<Rendering> renderings;
    List<Rendering> errorRenderings;


    public SimpleRenderingFactory(){
        renderings = new ArrayList<Rendering>();
        errorRenderings = new ArrayList<Rendering>();
    }


    private Map<String,RenderingChain> pool = new HashMap<String, RenderingChain>();

    private RenderingChain createRendering(Class<?> targetClass, Method method,String keyPrefix,List<Rendering> src){
        StringBuilder sb = new StringBuilder(keyPrefix);
        List<Rendering> list = new ArrayList<Rendering>();
        for(Rendering rendering : src){
            if(rendering.shouldHandle(targetClass,method)){
                sb.append(rendering.getUid());
                list.add(rendering);
            }
        }
        String key = sb.toString();
        RenderingChain  rendering = pool.get(key);
        if(rendering==null){
            GroupRendering groupRendering = new GroupRendering(list.toArray(new Rendering[list.size()]));
            rendering = groupRendering;
            pool.put(key, rendering);
        }
        return rendering;
    }

    @Override
    public synchronized RenderingChain createRendering(Class<?> targetClass, Method method) {
        return createRendering(targetClass,method,"",renderings);
    }

    @Override
    public synchronized RenderingChain createExceptionRendering(Class<?> targetClass, Method method) {
        return createRendering(targetClass,method,"error",errorRenderings);
    }

    @Override
    public void add(int index, Rendering rendering) {
        this.renderings.add(index,rendering);
    }

    @Override
    public void addError(int index, Rendering rendering) {
        this.errorRenderings.add(index,rendering);
    }

    @Override
    public void add(Rendering rendering) {
        this.renderings.add(rendering);
    }

    @Override
    public void addError(Rendering rendering) {
        this.errorRenderings.add(rendering);
    }
}
