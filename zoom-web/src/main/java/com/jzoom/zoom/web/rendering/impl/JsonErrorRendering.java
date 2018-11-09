package com.jzoom.zoom.web.rendering.impl;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.common.utils.MapUtils;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.exception.StatusException;
import com.jzoom.zoom.web.rendering.Rendering;
import com.jzoom.zoom.web.utils.ResponseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;

public class JsonErrorRendering implements Rendering {
	
	private static final Log log = LogFactory.getLog(JsonErrorRendering.class);

	@Override
	public boolean render(ActionContext context) throws Exception {
		HttpServletResponse response = context.getResponse();
		Object result = context.getRenderObject();
		if(result instanceof Throwable) {
			Throwable exception = Classes.getCause( (Throwable)result );
			if(exception instanceof StatusException) {
				StatusException statusException = (StatusException)exception;
				response.setStatus(statusException.getStatus());
				ResponseUtils.json(response, MapUtils.asMap("code", statusException.getClass().getName(), "error",statusException.getError() )  );
				return true;
			}

            response.setStatus(500);
            ResponseUtils.json(response, MapUtils.asMap("code", exception.getClass().getName(),
                    "error", exception.getMessage()));
		}else {
			ResponseUtils.json(response, result);
		}
		
		return true;
	}

}
