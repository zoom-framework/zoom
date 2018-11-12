package org.zoomdev.zoom.web.rendering.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.view.TemplateView;

public class TemplateViewRendering implements Rendering {

    @Override
    public boolean render(ActionContext context) throws Exception {

        Object result = context.getRenderObject();
        if (result instanceof TemplateView) {
            TemplateView templateView = (TemplateView) result;
            if (StringUtils.isEmpty(templateView.getPath())) {
                //没有修改默认
            }

            if (StringUtils.isEmpty(templateView.getEngine())) {
                //没有修改默认引擎
            }

            return true;
        }


        return false;
    }

}
