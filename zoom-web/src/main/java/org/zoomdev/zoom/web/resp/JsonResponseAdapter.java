package org.zoomdev.zoom.web.resp;

/**
 * 
 * 
 * 将controller方法返回的值适配成前端将要返回的值,比如加上状态码等
 * @author jzoom
 *
 */
public interface JsonResponseAdapter {
	/**
	 * 适配成功
	 * @param result
	 * @return
	 */
	Object adapterOk( Object result );
	
	/**
	 * 适配异常
	 * @param exception
	 * @return
	 */
	Object adapterException(Throwable exception);
}
