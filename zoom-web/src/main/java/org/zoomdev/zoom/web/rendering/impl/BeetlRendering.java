package org.zoomdev.zoom.web.rendering.impl;

import org.beetl.core.*;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.core.resource.WebAppResourceLoader;
import org.zoomdev.zoom.common.config.ConfigReader;
import org.zoomdev.zoom.common.utils.PathUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class BeetlRendering extends TemplateRendering {

    public static BeetlRendering createStringRendering() {

        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        try {
            return new BeetlRendering(resourceLoader, Configuration.defaultConfiguration());
        } catch (IOException e) {
            throw new RuntimeException("创建BeetlRendering失败", e);
        }

    }

    public static BeetlRendering createFileRendering() {
        File path;
        String key = ConfigReader.getDefault().getString("template.path");
        if (key == null) {
            //在WEB-INF的templates下面
            path = new File(PathUtils.getWebInfPath(""), "templates");
        } else {
            path = PathUtils.resolve(key);
        }

        return createFileRendering(path);
    }

    public static BeetlRendering createClassPathRendering() {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(BeetlRendering.class.getClassLoader(), "templates/");
        try {
            return new BeetlRendering(resourceLoader, Configuration.defaultConfiguration());
        } catch (IOException e) {
            throw new RuntimeException("创建BeetlRendering失败", e);
        }
    }

    private static BeetlRendering createFileRendering(File root) {
        ResourceLoader resourceLoader = new WebAppResourceLoader(root.getAbsolutePath());
        try {
            return new BeetlRendering(resourceLoader, Configuration.defaultConfiguration());
        } catch (IOException e) {
            throw new RuntimeException("创建BeetlRendering失败", e);
        }
    }


    public GroupTemplate group;

    public BeetlRendering(ResourceLoader loader, Configuration cfg) {
        group = new GroupTemplate(loader, cfg);
        group.setErrorHandler(new ErrorHandler(){

            @Override
            public void processExcption(BeetlException beeExceptionos, Writer writer) {
                beeExceptionos.printStackTrace();
                throw beeExceptionos;
            }
        });
    }

    @Override
    protected void render(HttpServletRequest request, HttpServletResponse response, String path,
                          Map<String, Object> data) throws Exception {

        Template template = group.getTemplate(path + getExt());
        template.binding(data);
        template.renderTo(response.getOutputStream());
    }

}
