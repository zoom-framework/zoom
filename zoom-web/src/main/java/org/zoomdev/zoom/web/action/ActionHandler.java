package org.zoomdev.zoom.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 一个路由单元
 * url+METHOD对应到一个ActionHandler
 *
 * @author jzoom
 */
public interface ActionHandler {

    /**
     * 处理请求
     *
     * @param request
     * @param response
     * @return
     */
    boolean handle(HttpServletRequest request, HttpServletResponse response);

    /**
     * 是否支持某个方法
     *
     * @param method
     * @return
     */
    boolean supportsHttpMethod(String method);

    /**
     * 路由映射 带有{}
     *
     * @return
     */
    String getMapping();

    /**
     * 获取支持的所有方法
     *
     * @return
     */
    String[] getMethods();

    /**
     * 获取调用参数名称
     *
     * @return
     */
    String[] getPathVariableNames();
}
