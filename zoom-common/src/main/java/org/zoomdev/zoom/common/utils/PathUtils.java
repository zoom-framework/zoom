package org.zoomdev.zoom.common.utils;

import org.zoomdev.zoom.common.exceptions.ZoomException;

import java.io.File;
import java.net.URL;


public class PathUtils {

    private static String webRootPath;
    private static String rootClassPath;

    /**
     * 获取网站根目录
     *
     * @return
     */
    public static String getWebRootPath() {
        if (webRootPath == null) {
            try {
                URL url = PathUtils.class.getResource("/");
                if (url == null) {
                    return null;
                }
                String path = PathUtils.class.getResource("/").toURI().getPath();
                // System.out.println("path:"+path);
                webRootPath = new File(path).getParentFile().getParentFile().getCanonicalPath();
                if (!webRootPath.endsWith(File.separator)) {
                    webRootPath = new StringBuilder(webRootPath).append(File.separator).toString();
                }
            } catch (Exception e) {
                throw new ZoomException(e);
            }
        }
        return webRootPath;

    }

    /**
     * @param name
     * @return
     */
    public static File getWebInfPath(String name) {
        File file = new File(PathUtils.getWebRootPath() + "WEB-INF" + File.separator + name);
        if (!file.exists()) {
            file = new File(PathUtils.getWebRootPath() + "src/main/webapp/WEB-INF" + File.separator + name);
        }
        return file;
    }

    /**
     * 解析路径
     * 两种情况：
     * 1、WEB-INF为根目录计算
     * 2、web root为根目录计算。
     *
     * @param key
     * @return
     */
    public static File resolve(String key) {

        return null;
    }
}
