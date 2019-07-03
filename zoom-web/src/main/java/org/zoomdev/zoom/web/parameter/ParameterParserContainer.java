package org.zoomdev.zoom.web.parameter;

import org.zoomdev.zoom.web.action.ActionContext;


/**
 * 承载 ParameterAdapter
 */
public interface ParameterParserContainer {


    boolean shouldAdapt(ActionContext context);

    /**
     * @return
     */
    Object[] parse(ActionContext context) throws Throwable;


}
