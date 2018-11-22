package org.zoomdev.zoom.web.rendering.impl;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.PathUtils;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.exception.StatusException;
import org.zoomdev.zoom.web.rendering.Rendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author jzoom
 */
public abstract class TemplateRendering implements Rendering {

    /**
     * 模板后缀如.html
     */
    private String ext;

    /**
     * 获取默认模板位置(WEB-INF/templates)
     *
     * @return
     */
    public static File getDefaultPath() {
        return new File(PathUtils.getWebInfPath(""),"templates");
    }


    public String getExt() {

        return ext == null ? ".html" : ext;
    }

    public void setExt(String ext) {
        if (!ext.startsWith(".")) {
            ext = "." + ext;
        }
        this.ext = ext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean render(ActionContext context) throws Exception {
        Object result = context.getRenderObject();
        HttpServletResponse response = context.getResponse();


        HttpServletRequest request = context.getRequest();
        String path = context.getAction().getPath();
        Map<String, Object> data = null;
        if (result instanceof Map) {
            data = (Map<String, Object>) result;
            data = merge(data, context);
        } else if (result instanceof String) {
            path = (String) result;
            data = merge(data, context);
        } else if (result instanceof Throwable) {
            if (result instanceof StatusException) {
                response.setStatus(((StatusException) result).getStatus());
            } else {
                response.setStatus(500);
            }
            path = "/error";
            data = new HashMap<String, Object>();
            Throwable error = (Throwable) result;
            data.put("message", error.getMessage());
            data.put("error", Classes.formatStackTrace(error));
        }

        render(request, response, path, data);
        return true;
    }



    /**
     * 将request中的所有attribute合并到一个map中
     *
     * @param data
     * @param request
     */
    public static void merge(Map<String, Object> data, HttpServletRequest request) {
        assert (data != null && request != null);
        Enumeration<String> enumeration = request.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            data.put(key, request.getAttribute(key));
        }
    }

    private Map<String, Object> merge(Map<String, Object> data, ActionContext context) {
        if (data == null) {
            data = new HashMap<String, Object>();
        }

        merge(data, context.getRequest());

        if (context.getData() != null) {
            data.putAll(context.getData());
        }



        return data;
    }

    /**
     * @param request
     * @param response
     * @param path
     * @param data
     */
    protected abstract void render(HttpServletRequest request, HttpServletResponse response, String path,
                                   Map<String, Object> data) throws Exception;

}
