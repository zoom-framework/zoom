package org.zoomdev.zoom.web.parameter.pre.impl;

import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.PreParameterParser;
import org.zoomdev.zoom.web.utils.UploadUtils;

public class UploadPreParamParser implements PreParameterParser {

	@Override
	public Object preParse(ActionContext context) throws Exception {
		return UploadUtils.doUpload(context.getRequest());
	}

	@Override
	public boolean shouldParse(String contentType) {
		return contentType!=null && contentType.startsWith("multipart/form-data;");
	}

}
