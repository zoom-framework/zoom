package org.zoomdev.zoom.web.rendering;

import org.zoomdev.zoom.web.rendering.impl.TemplateRendering;
import org.zoomdev.zoom.web.rendering.impl.TemplateRendering;

/**
 * 模板引擎
 * @author jzoom
 *
 */
public interface TemplateEngineManager {
	/**
	 * 根据名称能获取模板引擎
	 * @param name
	 * @return
	 */
	TemplateRendering getEngine(String name);
	
	/**
	 * 注册模板引擎
	 * @param name				引擎名称
	 * @param rendering			渲染器
	 */
	void register(String name,TemplateRendering rendering);
}
