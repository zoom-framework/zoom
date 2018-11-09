package com.jzoom.zoom.plugin;

public interface Plugin {


	/**
	 * 名称
	 * @return
	 */
	String getTitle();
	
	/**
	 * 版本
	 * @return
	 */
	String getVersion();
	
	/**
	 * id
	 * @return
	 */
	String getUId();
	
	/**
	 * 插件描述
	 * @return
	 */
	String getDescription();
	
	void shutdown(PluginHost host);
	
	void startup(PluginHost host) throws PluginException;

	void install(PluginHost pluginHost);

	void uninstall(PluginHost pluginHost);

	boolean isInstalled();
	
	
}
