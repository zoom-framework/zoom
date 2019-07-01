package org.zoomdev.zoom.web.view.impl;

import org.codehaus.jackson.map.ObjectMapper;
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

    private final boolean pretty;
    private Object data;


    private int status;

    /**
     * 需要转化的object
     *
     * @param data
     */
    public JsonView(Object data) {
        this(200, data,false);
    }

    public JsonView(Object data,boolean pretty) {
        this(200, data,pretty);
    }
    /**
     * 自定义http status用这个构造函数
     *
     * @param status
     * @param data
     */
    public JsonView(int status, Object data,boolean pretty) {
        this.status = status;
        this.data = data;
        this.pretty = pretty;
    }
    static ObjectMapper mapper = new ObjectMapper();
    @Override
    public void render(HttpServletResponse response) throws Exception {
        response.setStatus(status);
        response.setHeader("Content-Type", "application/json");

        if(pretty){
            ResponseUtils.write(response,  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        }else {
            ResponseUtils.write(response, JSON.stringify(data));
        }

    }

}
