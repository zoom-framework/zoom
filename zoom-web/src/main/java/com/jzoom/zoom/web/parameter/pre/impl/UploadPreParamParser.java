package com.jzoom.zoom.web.parameter.pre.impl;

import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.PreParameterParser;
import com.jzoom.zoom.web.utils.UploadUtils;

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
