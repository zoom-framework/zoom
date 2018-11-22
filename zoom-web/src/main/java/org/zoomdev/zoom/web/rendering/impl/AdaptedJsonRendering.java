package org.zoomdev.zoom.web.rendering.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.rendering.Rendering;
import org.zoomdev.zoom.web.resp.JsonResponseAdapter;
import org.zoomdev.zoom.web.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;

public class AdaptedJsonRendering implements Rendering {

    private JsonResponseAdapter adapter;

    public AdaptedJsonRendering(JsonResponseAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean render(ActionContext context) throws Exception {

        HttpServletResponse response = context.getResponse();

        Object result = context.getResult();
        Throwable exception = context.getException();
        if(exception!=null){
            result = adapter.adapterException(exception);
        }else{
            result = adapter.adapterOk(result);
        }
        ResponseUtils.json(response, result);
        return true;

    }


}
