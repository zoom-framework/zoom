package org.zoomdev.zoom.web.parameter;

import org.zoomdev.zoom.web.action.ActionContext;

/**
 * 对参数预处理解析之后的结果处理
 *
 * @author renxueliang
 */
public interface ParameterParser {

    /**
     * @return
     */
    Object[] parse(ActionContext context) throws Throwable;


}
