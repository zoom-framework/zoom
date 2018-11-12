package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.view.View;

public class ViewRendering implements Rendering {

    @Override
    public boolean render(ActionContext context) throws Exception {
        if (context.getRenderObject() instanceof View) {
            ((View) context.getResult()).render(context.getResponse());
            return true;
        }
        return false;
    }

}
