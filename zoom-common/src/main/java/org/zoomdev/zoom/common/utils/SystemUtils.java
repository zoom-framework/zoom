package org.zoomdev.zoom.common.utils;

public class SystemUtils {

    /**
     * 获取java.class.path，并分解
     *
     * @return
     */
    public static String[] getPath() {
        String path = System.getProperty("java.class.path");
        String[] parts = path.split(":");

        return parts;
    }
}
