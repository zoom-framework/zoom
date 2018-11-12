package org.zoomdev.zoom.plugin.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.plugin.PluginException;
import org.zoomdev.zoom.plugin.PluginHolder;
import org.zoomdev.zoom.plugin.PluginHost;
import org.zoomdev.zoom.web.exception.StatusException;
import org.zoomdev.zoom.web.utils.WebUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginService {

    private static final Log log = LogFactory.getLog(PluginService.class);

    @Inject(value = "admin")
    private Dao dao;

    @Inject
    private PluginHost pluginHost;

    // 查询所有的插件
    public synchronized void startup() {
        WebUtils.runAfterAsync(new Runnable() {
            @Override
            public void run() {
                List<Record> plugins = dao.table("sys_plugin").find();
                for (Record record : plugins) {
                    try {
                        pluginHost.load(new URL(record.getString("uri")));
                    } catch (Exception e) {
                        log.warn("插件加载失败" + record.getString("id"), e);
                    }
                }
                try {
                    pluginHost.startup();
                } catch (PluginException e) {
                    log.error("插件服务启动失败");
                }
            }
        });

    }


    private PluginHolder loadPlugin(String uri) {
        // 不存在,加载
        try {
            URL url = new URL(uri);
            return pluginHost.load(url);

        } catch (MalformedURLException e) {
            throw new StatusException.ApiError("插件url不合法");
        } catch (PluginException e) {
            e.printStackTrace();
            throw new StatusException.ApiError("加载插件失败");
        }
    }

    private PluginHolder getAndLoadWhenNotExists(String id, String uri) {
        PluginHolder pluginHolder = pluginHost.getPluginById(id);
        if (pluginHolder == null) {
            pluginHolder = loadPlugin(uri);
        }
        return pluginHolder;
    }

    public synchronized void install(String id) {
        Record record = dao.table("sys_plugin").where("id", id).get();
        if (record == null) {
            throw new StatusException.ApiError("找不到本插件");
        }
        if (record.getBoolean("installed")) {
            throw new StatusException.ApiError("插件已经安装，请不要重复安装");
        }
        String uri = record.getString("uri");
        PluginHolder pluginHolder = getAndLoadWhenNotExists(id, uri);
        if (pluginHolder.isInstalled()) {
            dao.table("sys_plugin").where("id", id).set("installed", 1).update();
            return;
        }
        pluginHolder.install(pluginHost);
    }

    private static final Pattern PATTERN = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)");

    /**
     * 两个版本进行比较，版本的格式必须为 1.0.2
     *
     * @param src
     * @param dest
     * @return
     */
    private int compareVersion(String src, String dest) {
        Matcher srcMatcher = PATTERN.matcher(src);
        if (!srcMatcher.matches()) {
            throw new StatusException.ApiError("版本的格式必须为1.0.0");
        }
        Matcher destMatcher = PATTERN.matcher(dest);
        if (!destMatcher.matches()) {
            throw new StatusException.ApiError("版本的格式必须为1.0.0");
        }

        for (int i = 1; i <= 3; ++i) {
            String srcVer = srcMatcher.group(i);
            String destVer = destMatcher.group(i);
            int nSrcVer = Integer.parseInt(srcVer);
            int nDestVer = Integer.parseInt(destVer);
            if (nSrcVer > nDestVer) {
                return 1;
            } else if (nSrcVer == nDestVer) {
                continue;
            }
            return -1;
        }

        return 0;

    }

    public Record getRecord(String id) {
        return dao.table("sys_plugin").where("id", id).get();
    }

    public synchronized void add(String uri) {
        PluginHolder plugin = loadPlugin(uri);
        String id = plugin.getUid();
        Record record = getRecord(id);
        if (record == null) {
            dao.table("sys_plugin").set("uri", uri).set("id", id).set("title", plugin.getTitle())
                    .set("description", plugin.getDescription()).set("installed", plugin.isInstalled() ? 1 : 0)
                    .set("version", plugin.getVersion()).insert();
        } else {

            if (compareVersion(plugin.getVersion(), record.getString("version")) <= 0) {
                throw new StatusException.ApiError("上传插件的版本为" + plugin.getVersion() + "应该高于原来的版本");
            }

            dao.table("sys_plugin").where("id", id).set("uri", uri).set("id", id).set("title", plugin.getTitle())
                    .set("description", plugin.getDescription()).set("installed", plugin.isInstalled() ? 1 : 0)
                    .set("version", plugin.getVersion()).update();
        }
    }

    public boolean isRunning(String id) {
        PluginHolder plugin = pluginHost.getPluginById(id);
        if (plugin == null) {
            return false;
        }
        return plugin.isRunning();
    }

    public Record getAndChek(String id) {
        Record record = getRecord(id);
        if (record == null) {
            throw new StatusException.ApiError("找不到本插件");
        }
        return record;
    }

    public void startup(String id) {
        Record record = getAndChek(id);

        PluginHolder plugin = getAndLoadWhenNotExists(id, record.getString("uri"));
        if (!plugin.isInstalled()) {
            // 这里暂时先不一次性加载
            throw new StatusException.ApiError("本插件未安装，请先安装");
        }

        try {
            pluginHost.startup(plugin);
        } catch (PluginException e) {
            throw new StatusException.ApiError("插件加载失败");
        }

    }

    public void shutdown(String id) {
        Record record = getAndChek(id);
        PluginHolder plugin = getAndLoadWhenNotExists(id, record.getString("uri"));
        if (!plugin.isInstalled()) {
            // 这里暂时先不一次性加载
            throw new StatusException.ApiError("本插件未安装，请先安装");
        }

        try {
            pluginHost.shutdown(plugin, false);
            dao.table("sys_plugin").where("id", id).set("running", 0).update();
        } catch (PluginException e) {
            throw new StatusException.ApiError("插件加载失败");
        }
    }

    public void uninstall(String id) {
        Record record = getAndChek(id);
        PluginHolder plugin = getAndLoadWhenNotExists(id, record.getString("uri"));
        if (!plugin.isInstalled()) {
            return;
        }

        try {
            pluginHost.shutdown(plugin, false);
            pluginHost.uninstall(plugin);
            dao.table("sys_plugin").where("id", id).set("running", 0).set("installed", 0).update();
        } catch (PluginException e) {
            throw new StatusException.ApiError("插件加载失败");
        }
    }

}
