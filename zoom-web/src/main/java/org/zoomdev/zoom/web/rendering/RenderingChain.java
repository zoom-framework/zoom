package org.zoomdev.zoom.web.rendering;

import org.zoomdev.zoom.web.action.ActionContext;

public interface RenderingChain {
    /**
     * @param context {@link ActionContext}
     * @return 如果已经渲染，返回true,否则返回false
     * @throws Exception
     */
    boolean render(ActionContext context) throws Exception;

}
