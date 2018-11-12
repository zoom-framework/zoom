package org.zoomdev.zoom.common.logger;


/***
 * 对 org.apache.commons.logging.Log的封装
 * 除非都想用格式化的输出，不然还是直接用org.apache.commons.logging.Log
 *
 * @author jzoom
 *
 */
public interface Logger {

    void info(String format, Object... args);

    void debug(String format, Object... args);

    void error(String format, Object... args);

    void error(Throwable exception, String format, Object... args);

    void warn(String format, Object... args);

    boolean isDebugEnabled();

    boolean isTraceEnabled();

}
