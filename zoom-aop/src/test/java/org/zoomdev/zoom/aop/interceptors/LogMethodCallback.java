package org.zoomdev.zoom.aop.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.aop.MethodCallback;
import org.zoomdev.zoom.common.logger.Logger;
import org.zoomdev.zoom.common.logger.Loggers;

import java.lang.reflect.Method;

/**
 * 提供详细的日志
 *
 * @author jzoom
 */
public class LogMethodCallback extends MethodCallback {

    private static final Logger log = Loggers.getLogger();

    @Override
    protected void before(Method method, Object[] args) {
        log.info("Before method:[%s] args:[%s]", method, StringUtils.join(args, ","));
    }

    @Override
    protected void error(Method method, Object[] args, Throwable throwable) {
        log.error(throwable, "Error method:[%s] args:[%s]", method, StringUtils.join(args, ","));
    }

    @Override
    protected void complete(Method method) {
        log.info("Complete method:[%s]", method);
    }


    @Override
    protected void after(Method method, Object[] args, Object result) {
        log.info("after method:[%s] args:[%s] result:[%s]", method, StringUtils.join(args, ","), result);
    }
}
