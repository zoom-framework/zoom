package org.zoomdev.zoom.plugin.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.plugin.NotSupportException;
import org.zoomdev.zoom.plugin.PluginException;
import org.zoomdev.zoom.plugin.PluginHolder;
import org.zoomdev.zoom.plugin.PluginHost;
import org.zoomdev.zoom.web.router.Router;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ZoomPluginHost implements PluginHost {

    private List<PluginHolder> holders;

    private IocContainer ioc;

    private Router router;


    private static final Log logger = LogFactory.getLog(PluginHost.class);

    private List<PluginHolder> runningPlugins;


    public ZoomPluginHost(IocContainer ioc,Router router) {
        holders = new ArrayList<PluginHolder>();
        runningPlugins = new ArrayList<PluginHolder>();
        this.ioc = ioc;
        this.router = router;
    }

    public synchronized PluginHolder load(URL plugin) throws PluginException {
        PluginHolder holder = new SimplePluginHolder(plugin);
        holder.load();
        holders.add(holder);
        return holder;
    }

    public synchronized PluginHolder getPluginById(String id) {
        for (PluginHolder pluginHolder : holders) {
            if (id.equals(pluginHolder.getUid())) {
                return pluginHolder;
            }
        }
        return null;
    }

    public synchronized void startup(PluginHolder pluginHolder) {
        try {
            pluginHolder.startup(this);
            runningPlugins.add(pluginHolder);
        } catch (Throwable e) {
            logger.error("Startup plugin fail!", e);
        }
    }

    @Override
    public void shutdown(PluginHolder plugin, boolean ignoreError) throws PluginException {
        assert (plugin != null);
        plugin.shutdown(this, ignoreError);
        runningPlugins.remove(plugin);
    }

    @Override
    public IocContainer getIoc() {
        return ioc;
    }

    @Override
    public Router getRouter() {
        return router;
    }

    @Override
    public synchronized void install(PluginHolder pluginHolder) throws PluginException {

        pluginHolder.install(this);

    }

    @Override
    public synchronized void uninstall(PluginHolder pluginHolder) throws PluginException {
        pluginHolder.uninstall(this);
    }

    public synchronized void startup() throws PluginException {
        for (PluginHolder pluginHolder : holders) {
            if (pluginHolder.isInstalled() && !pluginHolder.isRunning()) {
                startup(pluginHolder);
            }
        }

    }


    @Override
    public synchronized void shutdown(boolean ignoreError) {
        for (PluginHolder plugin : runningPlugins) {
            try {
                plugin.shutdown(this, ignoreError);
            } catch (Throwable e) {

            }

        }
    }

    @Override
    public void update(String event, String sender, Object data) throws NotSupportException {

    }


}
