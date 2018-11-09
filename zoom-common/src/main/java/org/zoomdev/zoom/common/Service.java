package org.zoomdev.zoom.common;

/**
 * 服务,与插件的差别是插件的启动调用早于服务
 * @author jzoom
 *
 */
public interface Service {
	/**
	 * 启动
	 */
	void startup() throws Exception;
	
	/**
	 * 停止
	 */
	void shutdown();
	
}
