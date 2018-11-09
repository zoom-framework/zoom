package org.zoomdev.zoom.web.view;

import org.zoomdev.zoom.web.view.impl.BytesView;
import org.zoomdev.zoom.web.view.impl.FileView;
import org.zoomdev.zoom.web.view.impl.StringView;

import javax.servlet.http.HttpServletResponse;

/**
 * 提供一种轻量的渲染方法
 * @see FileView
 * @see org.zoomdev.zoom.web.view.impl.JsonView
 * @see BytesView
 * @see org.zoomdev.zoom.web.view.impl.RedirectView
 * @see StringView
 * 
 * @author jzoom
 *
 */
public interface View {
	
	void render( HttpServletResponse response) throws Exception;
	
}
