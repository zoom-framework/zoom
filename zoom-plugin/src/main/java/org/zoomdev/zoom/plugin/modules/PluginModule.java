package org.zoomdev.zoom.plugin.modules;

import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.res.ClassResolvers;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.plugin.PluginHost;
import org.zoomdev.zoom.plugin.annotations.PluginEnable;
import org.zoomdev.zoom.plugin.impl.SimplePluginHost;
import org.zoomdev.zoom.plugin.services.PluginService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Module(PluginEnable.class)
public class PluginModule {

	
	private static final Log log = LogFactory.getLog(PluginModule.class);
	
	@Inject
	private PluginService pluginService;

	@Inject(value = "admin")
	private Dao admin;
	
	@Inject
	public void startup() {
		log.info("=============Plugin startup============");
		pluginService.startup();
	}
	
	
	@IocBean
	public PluginHost getPluginHost() {
		return new SimplePluginHost();
	}
	
}
