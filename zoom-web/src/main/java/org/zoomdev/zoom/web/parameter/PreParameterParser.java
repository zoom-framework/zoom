package org.zoomdev.zoom.web.parameter;


import org.zoomdev.zoom.web.action.ActionContext;

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
