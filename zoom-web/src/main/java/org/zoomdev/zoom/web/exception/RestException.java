package org.zoomdev.zoom.web.exception;

/**
 * Restful 风格api 抛出异常的接口
 */
public interface RestException {

    /**
     * HTTP 状态码
     * @return
     */
    int getStatus();

    /**
     * 异常编码
     * @return
     */
    String getCode();

    /**
     * 异常描述
     * @return
     */
    String getError();
}
