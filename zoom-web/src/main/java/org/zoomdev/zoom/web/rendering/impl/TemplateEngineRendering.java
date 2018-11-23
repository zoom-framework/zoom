package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.web.WebConfig;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;

import java.lang.reflect.Method;

public class TemplateEngineRendering implements Rendering {

    private final TemplateEngineManager templateEngineManager;
    private final WebConfig webConfig;

    public TemplateEngineRendering(
            TemplateEngineManager templateEngineManager,
            WebConfig config){
        this.templateEngineManager = templateEngineManager;
        this.webConfig = config;

    }
    @Override
    public String getUid() {
        return "template";
    }

    @Override
    public boolean render(ActionContext context) throws Exception {
        Object data = context.getRenderObject();
        String ext;
        if(data instanceof String){
            String path  = (String)data;
            int n;
            if((n=path.lastIndexOf('.')) > 0){
                ext = path.substring(n+1);
                path = path.substring(0,n);
                context.setRenderObject(path);
            }else{
                ext = webConfig.getTemplateExt();
            }
        }else{
            ext = webConfig.getTemplateExt();
        }

        TemplateRendering rendering = templateEngineManager.getEngine(ext);
        if(rendering==null){
            throw new ZoomException("找不到渲染引擎"+ext);
        }
        return rendering.render(context);
    }

    @Override
    public boolean shouldHandle(Class<?> targetClass, Method method) {
        return true;
    }


}
