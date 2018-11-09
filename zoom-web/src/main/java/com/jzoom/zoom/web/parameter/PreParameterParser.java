package com.jzoom.zoom.web.parameter;


import com.jzoom.zoom.web.action.ActionContext;

/**
 * 
 * 
 * 
 * @author renxueliang
 *
 */
public interface PreParameterParser {
	/**
	 * 
	 * @param context		
	 * @return                返回解析后的结果
	 * @throws Exception
	 */
	Object preParse(ActionContext context)  throws Exception;
	
	boolean shouldParse( String contentType );
}
