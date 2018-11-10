package org.zoomdev.zoom.plugin;

import java.net.URL;

public interface PluginHolder {

	/**
	 * 加载信息
	 * 
	 * @throws PluginException
	 */
	void load() throws PluginException;

	void install(PluginHost pluginHost);

	void uninstall(PluginHost pluginHost);

	boolean isInstalled();

	/**
	 * 启动
	 * 
	 * @param host
	 * @throws PluginException
	 */
	void startup(PluginHost host) throws PluginException;

	void shutdown(PluginHost host,boolean ignoreError);

	boolean isRunning();

	String getUid();

	URL getUrl();
	
	String getTitle();
	
	String getVersion();
	
	String getDescription();

}
