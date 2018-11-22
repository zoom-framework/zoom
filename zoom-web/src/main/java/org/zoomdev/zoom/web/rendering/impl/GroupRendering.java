package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;

public class GroupRendering implements Rendering {

    private Rendering[] renderings;

    public GroupRendering(Rendering... renderings) {
        this.renderings = renderings;
    }

    @Override
    public boolean render(ActionContext context) throws Exception {
        for (Rendering rendering : renderings) {
            if (rendering.render(context))
                return true;
        }
        return false;
    }

}
