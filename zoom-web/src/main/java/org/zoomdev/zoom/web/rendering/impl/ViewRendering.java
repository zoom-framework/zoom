package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.view.View;

import java.lang.reflect.Method;

public class ViewRendering implements Rendering {

    @Override
    public boolean render(ActionContext context) throws Exception {

        Object renderObject;
        if ((renderObject = context.getRenderObject()) instanceof View) {
            ((View) renderObject).render(context.getResponse());
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldHandle(Class<?> targetClass, Method method) {
        return true;
    }

    @Override
    public String getUid() {
        return "view";
    }

}
