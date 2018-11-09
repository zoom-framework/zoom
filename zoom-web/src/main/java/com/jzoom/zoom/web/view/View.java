package com.jzoom.zoom.web.view;

import javax.servlet.http.HttpServletResponse;

/**
 * 提供一种轻量的渲染方法
 * @see com.jzoom.zoom.web.view.impl.FileView
 * @see com.jzoom.zoom.web.view.impl.JsonView
 * @see com.jzoom.zoom.web.view.impl.BytesView
 * @see com.jzoom.zoom.web.view.impl.RedirectView
 * @see com.jzoom.zoom.web.view.impl.StringView
 * 
 * @author jzoom
 *
 */
public interface View {
	
	void render( HttpServletResponse response) throws Exception;
	
}
