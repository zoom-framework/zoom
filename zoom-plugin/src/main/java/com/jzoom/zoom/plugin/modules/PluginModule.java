package com.jzoom.zoom.plugin.modules;

import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.IocBean;
import com.jzoom.zoom.common.annotations.Module;
import com.jzoom.zoom.common.res.ClassResolvers;
import com.jzoom.zoom.dao.Dao;
import com.jzoom.zoom.plugin.PluginHost;
import com.jzoom.zoom.plugin.annotations.PluginEnable;
import com.jzoom.zoom.plugin.impl.SimplePluginHost;
import com.jzoom.zoom.plugin.services.PluginService;
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
	public PluginHost getPluginHost(ClassResolvers resolvers) {
		return new SimplePluginHost(resolvers);
	}
	
}
