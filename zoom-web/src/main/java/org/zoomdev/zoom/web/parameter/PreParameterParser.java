package org.zoomdev.zoom.web.parameter;


import org.zoomdev.zoom.web.action.ActionContext;

/**
 * @author JZoom
 */
public interface PreParameterParser {
    /**
     * @param context
     * @return 返回解析后的结果
     * @throws Exception
     */
    Object preParse(ActionContext context) throws Exception;

    /**
     * 是否应该对本http消息进行解析
     *
     * @param contentType
     * @return
     */
    boolean shouldParse(String contentType);
}
