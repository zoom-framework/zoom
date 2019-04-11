package org.zoomdev.zoom.web.view.impl;

import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.web.utils.ResponseUtils;
import org.zoomdev.zoom.web.view.View;

import javax.servlet.http.HttpServletResponse;

/**
 * 渲染一个json
 *
 * @author jzoom
 */
public class JsonView implements View {

    private Object data;


    private int status;

    /**
     * 需要转化的object
     *
     * @param data
     */
    public JsonView(Object data) {
        this(200, data);
    }

    /**
     * 自定义http status用这个构造函数
     *
     * @param status
     * @param data
     */
    public JsonView(int status, Object data) {
        this.status = status;
        this.data = data;
    }

    @Override
    public void render(HttpServletResponse response) throws Exception {
        response.setStatus(status);
        response.setHeader("Content-Type", "application/json");
        ResponseUtils.write(response, JSON.stringify(data));
    }

}
