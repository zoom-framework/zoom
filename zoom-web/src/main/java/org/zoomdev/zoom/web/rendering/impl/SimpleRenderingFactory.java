package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.RenderingFactory;
import org.zoomdev.zoom.web.rendering.RenderingFactoryManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleRenderingFactory implements RenderingFactoryManager {

    protected static Rendering viewRendering = new ViewRendering();


    private List<RenderingFactory> factoryList = new ArrayList<RenderingFactory>();

    public SimpleRenderingFactory(
            RenderingFactory... factories
    ) {
        Collections.addAll(factoryList,factories);
    }

    @Override
    public Rendering createRendering(Class<?> targetClass, Method method) {

        Rendering rendering = null;
        for(RenderingFactory factory : factoryList){
            rendering = factory.createRendering(targetClass,method);
            if(rendering!=null){
                break;
            }
        }
        if(rendering!=null){
            return new GroupRendering(viewRendering, rendering);
        }else {
            return viewRendering;
        }

    }


    @Override
    public void add(RenderingFactory factory) {
        factoryList.add(factory);
    }
}
