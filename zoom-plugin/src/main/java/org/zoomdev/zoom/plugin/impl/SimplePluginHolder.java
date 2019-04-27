package org.zoomdev.zoom.plugin.impl;

import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.web.utils.ClassResolvers;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.res.ResScanner.ClassRes;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.impl.ZoomIocContainer;
import org.zoomdev.zoom.plugin.Plugin;
import org.zoomdev.zoom.plugin.PluginException;
import org.zoomdev.zoom.plugin.PluginHolder;
import org.zoomdev.zoom.plugin.PluginHost;
import org.zoomdev.zoom.web.action.impl.SimpleActionBuilder;
import org.zoomdev.zoom.web.router.Router;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimplePluginHolder implements PluginHolder {

    private String url;
    private JarClassLoader classLoader;
    private Plugin plugin;
    private boolean running;
    ResScanner scanner = new ResScanner();

    public SimplePluginHolder(String url){
        this.url = url;

    }

    @Override
    public void load() throws PluginException {

        try {

            classLoader =JarClassLoader.fromUrls(new String[]{url});

            Set<String> keys = classLoader.getAllClassNames();
            for(String key : keys){
                scanner.appendRes(new ResScanner.BytesClassRes(
                        key,classLoader.getBytes(key),classLoader
                ));
            }


            List<ClassRes> plugnEntrys = scanner.findClass("*PluginEntry");
            if (plugnEntrys.size() != 1) {
                throw new PluginException("非plugin插件,插件必须有且只有一个类名称为PluginEntry的入口，并且实现接口org.zoomdev.zoom.plugin.Plugin");
            }

            Class<?> clazz = Class.forName(plugnEntrys.get(0).getName(), false, classLoader);
            Object pluginObject = clazz.newInstance();
            if (pluginObject instanceof Plugin) {
                this.plugin = (Plugin) pluginObject;
            } else {
                throw new PluginException("非plugin插件,插件必须实现接口org.zoomdev.zoom.plugin.Plugin");
            }
        } catch (Exception e) {
            if (e instanceof PluginException) {
                throw (PluginException) e;
            }
            throw new PluginException(e);
        }

    }

    @Override
    public synchronized boolean isRunning() {
        return running;
    }


    @Override
    public boolean isInstalled() {
        assert (plugin != null);
        return plugin.isInstalled();
    }

    private IocContainer ioc;

    private List<Router.RemoveToken> tokens;

    @Override
    public synchronized void startup(PluginHost host) throws PluginException {
        if (running) {
            return;
        }
        assert (plugin != null);
        try {
            IocContainer mainIoc = host.getIoc();
            ioc = new ZoomIocContainer(mainIoc,classLoader);
            tokens = new ArrayList<Router.RemoveToken>();;

            ClassResolvers classResolvers = new ClassResolvers(
                    new PluginConfigBuilder(ioc),
                    new SimpleActionBuilder(ioc, host.getRouter(),tokens)
            );
            classResolvers.visit(scanner);
            plugin.startup(host);
            running = true;
        } catch (Throwable t) {
            clear();
            throw new PluginException(t);
        }

    }

    void clear() {
        if (ioc != null) {
            ioc.destroy();
            ioc = null;
        }
        if (tokens != null) {
            for (Router.RemoveToken removeToken : tokens) {
                removeToken.remove();
            }
            tokens.clear();
            tokens = null;
        }
    }

    @Override
    public synchronized void shutdown(PluginHost host, boolean ignoreError) {
        assert (plugin != null);
        this.running = false;
        try {
            plugin.shutdown(host);
            clear();
        } catch (Throwable t) {
            //无论有没有关闭出错，都是清除
            if (ignoreError) {
                clear();
            }
        }
    }

    @Override
    public void install(PluginHost pluginHost) {
        assert (plugin != null);
        plugin.install(pluginHost);
    }

    @Override
    public void uninstall(PluginHost pluginHost) {
        assert (plugin != null);
        plugin.uninstall(pluginHost);
    }

    @Override
    public String getUid() {
        assert (plugin != null);
        return plugin.getUId();
    }

    @Override
    public String getDescription() {
        assert (plugin != null);
        return plugin.getDescription();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getTitle() {
        assert (plugin != null);
        return plugin.getTitle();
    }

    @Override
    public String getVersion() {
        assert (plugin != null);
        return plugin.getVersion();
    }


}
