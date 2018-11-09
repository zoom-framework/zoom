package com.jzoom.zoom.web.rendering;

/**
 * 对于Controller的返回结果，由match进行解析，并决定要不要用本渲染器进行渲染
 * @author jzoom
 *
 */
public interface PatternRendering extends Rendering{
	
	/**
	 * Controller返回的结果是否符合本渲染器的规则
	 * @param result
	 * @return
	 */
	boolean match(Object result);
	
}
