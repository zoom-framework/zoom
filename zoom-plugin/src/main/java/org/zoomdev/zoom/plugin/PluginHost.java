package org.zoomdev.zoom.plugin;

import java.net.URL;

import org.zoomdev.zoom.common.res.ClassResolvers;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.router.Router;

/**
 * 宿主程序
 * @author jzoom
 *
 */
public interface PluginHost {
	/**
	 * 
	 * @param event
	 * @param sender
	 * @param data
	 */
	void update(String event,String sender,Object data) throws NotSupportException;

	PluginHolder getPluginById(String id);
	
	PluginHolder load(URL url)  throws PluginException;
	
	/**
	 * 安装
	 * @param pluginHolder
	 * @throws PluginException
	 */
	void install(PluginHolder pluginHolder) throws PluginException;
	/**
	 * 卸载
	 * @param pluginHolder
	 * @throws PluginException
	 */
	void uninstall(PluginHolder pluginHolder) throws PluginException;
	
	void startup(PluginHolder pluginHolder) throws PluginException;
	
	
	void startup() throws PluginException;

	void shutdown(PluginHolder plugin,boolean ignoreError) throws PluginException;

	IocContainer getIoc();

	Router getRouter();
}