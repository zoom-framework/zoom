package org.zoomdev.zoom.common;

/**
 * 配置常量，存放application配置名称
 *
 * @author jzoom
 */
public class ConfigurationConstants {

    /**
     * 扫描jar的pattern {@see org.zoomdev.zoom.common.filter.pattern.PatternFilter }
     * 确定哪些jar要扫描:api-*.jar,xxx.jar
     */
    public static final String SCAN_JAR = "zoom.scan.jar";

    /**
     * 服务器的默认 Charset,默认utf-8
     */
    public static final String SERVER_ENCODING = "zoom.server.encoding";

    /**
     * 当前环境是什么，比如test/dev/production等
     */
    public static final String ENV = "zoom.env";

    public static final String UPLOAD_DIR = "zoom.upload.dir";

    public static final String UPLOAD_FORMAT = "zoom.upload.urlformat";
}
