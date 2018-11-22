package org.zoomdev.zoom.web;

public class WebConfig {

    /**
     * 默认模板后缀
     */
    private String ext;

    /**
     * 错误处理页面
     */
    private String error;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
