package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;

import java.lang.reflect.Method;

public class RedirectRendering implements Rendering {
    public static final String REDIRECT = "redirect:";
    public static final String FORWARD = "forward:";

    @Override
    public boolean render(ActionContext context) throws Exception {
        Object renderObject = context.getRenderObject();
        if (renderObject instanceof String) {
            String result = (String) renderObject;
            if (result.startsWith(RedirectRendering.REDIRECT)) {
                context.getResponse().sendRedirect(result.substring(RedirectRendering.REDIRECT.length()));
                return true;
            } else if (result.startsWith(FORWARD)) {
                throw new ZoomException("目前这个版本还不支持forward");
//                context.getRequest().getRequestDispatcher("")
//                        .forward(request,response);
            }
        }

        return false;
    }

    @Override
    public String getUid() {
        return "redirect";
    }

    @Override
    public boolean shouldHandle(Class<?> targetClass, Method method) {
        return true;
    }
}
