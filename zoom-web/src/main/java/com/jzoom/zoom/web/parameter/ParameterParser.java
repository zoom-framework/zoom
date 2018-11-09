package com.jzoom.zoom.web.parameter;

import com.jzoom.zoom.web.action.ActionContext;

/**
 * 对参数预处理解析之后的结果处理
 * @author renxueliang
 *
 */
public interface ParameterParser {
	
	interface HttpParameterDecoder{
		Object[] decode(ActionContext context)  throws Exception;
	}

	/**
	 * 
	 * @return
	 */
	Object[] parse(ActionContext context) throws Exception;

}
