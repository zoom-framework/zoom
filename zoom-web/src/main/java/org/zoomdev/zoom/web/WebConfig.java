package org.zoomdev.zoom.web;

public class WebConfig {

    /**
     * 默认模板后缀
     */
    private String templateExt = ".html";

    /**
     * 错误处理页面
     */
    private String errorPage = "/error";

    public String getTemplateExt() {
        return templateExt;
    }

    public void setTemplateExt(String templateExt) {
        this.templateExt = templateExt;
    }

    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

}
