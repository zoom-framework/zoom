package com.jzoom.zoom.plugin.impl;

import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.common.res.ResScanner;
import com.jzoom.zoom.common.res.ResScanner.ClassRes;
import com.jzoom.zoom.plugin.Plugin;
import com.jzoom.zoom.plugin.PluginException;
import com.jzoom.zoom.plugin.PluginHolder;
import com.jzoom.zoom.plugin.PluginHost;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class SimplePluginHolder implements PluginHolder {

	private URL url;
	private URLClassLoader classLoader;
	private Plugin plugin;
	private boolean running;
	ResScanner scanner = new ResScanner();
	public SimplePluginHolder(URL url) {
		this.url = url;
		classLoader = new URLClassLoader(new URL[] { url }, getClass().getClassLoader());
	}

	@Override
	public void load() throws PluginException {
		InputStream is = null;
		try {
			is = url.openStream();
			scanner.scan(is, classLoader);

            List<ClassRes> plugnEntrys = scanner.findClass("*PluginEntry");
			if(plugnEntrys.size() != 1) {
				throw new PluginException("非plugin插件,插件必须有且只有一个类名称为PluginEntry的入口，并且实现接口com.jzoom.zoom.plugin.Plugin");
			}

            Class<?> clazz = Class.forName(plugnEntrys.get(0).getName(), false, classLoader);
			Object pluginObject = clazz.newInstance();
			if (pluginObject instanceof Plugin) {
				this.plugin = (Plugin) pluginObject;
			} else {
				throw new PluginException("非plugin插件,插件必须实现接口com.jzoom.zoom.plugin.Plugin");
			}
		}catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
			throw new PluginException(e);
		} finally {
			Io.close(is);
		}

    }

	@Override
	public synchronized boolean isRunning() {
		return running;
	}

	
	@Override
	public boolean isInstalled() {
		assert(plugin!=null);
		return plugin.isInstalled();
	}

	@Override
	public synchronized void startup(PluginHost host) throws PluginException {
		if(running) {
			return;
		}
		assert(plugin!=null);
		host.getClassResolvers().visit(scanner);
		plugin.startup(host);
		running = true;
	}

	@Override
	public synchronized void shutdown(PluginHost host) {
		assert(plugin!=null);
		this.running = false;
		plugin.shutdown(host);
		
	}
	@Override
	public void install(PluginHost pluginHost) {
		assert(plugin!=null);
		plugin.install(pluginHost);
	}

	@Override
	public void uninstall(PluginHost pluginHost) {
		assert(plugin!=null);
		plugin.uninstall(pluginHost);
	}
	@Override
	public String getUid() {
		assert(plugin!=null);
		return plugin.getUId();
	}
	
	@Override
	public String getDescription() {
		assert(plugin!=null);
		return plugin.getDescription();
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public String getTitle() {
		assert(plugin!=null);
		return plugin.getTitle();
	}

	@Override
	public String getVersion() {
		assert(plugin!=null);
		return plugin.getVersion();
	}


	

}
