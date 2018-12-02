package org.zoomdev.zoom.web.action;

import javax.servlet.ServletException;
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
    boolean handle(HttpServletRequest request, HttpServletResponse response) throws ServletException;

    /**
     * 是否支持某个方法
     *
     * @param method
     * @return
     */
    boolean supportsHttpMethod(String method);


    /**
     * 获取调用参数名称
     *
     * @return
     */
    String[] getPathVariableNames();
}
