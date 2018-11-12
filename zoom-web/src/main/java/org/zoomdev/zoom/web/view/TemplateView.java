package org.zoomdev.zoom.web.view;

import java.util.Map;


public class TemplateView {

    private String engine;

    /**
     * template位置
     */
    private String path;

    /**
     * 渲染数据
     */
    private Map<String, Object> data;

    public TemplateView(String path, Map<String, Object> data) {
        this.path = path;
        this.data = data;
    }

    /**
     * @return
     */
    public String getPath() {
        return path;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }


}
