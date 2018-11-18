package org.zoomdev.zoom.web.rendering;

import org.zoomdev.zoom.web.action.ActionContext;

/**
 * 渲染器，用于渲染 {@link ActionContext#getResult}之后的结果
 * 渲染器在整个应用程序中只会初始化一次，所以内部的渲染方法必须是线程安全的
 *
 * @author jzoom
 */
public interface Rendering {

    /**
     * @param context {@link ActionContext}
     * @return 如果已经渲染，返回true,否则返回false
     * @throws Exception
     */
    boolean render(ActionContext context) throws Exception;

}
