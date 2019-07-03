package org.zoomdev.zoom.web.action;

import org.zoomdev.zoom.web.rendering.impl.TemplateRendering;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

public class MockTemplateRendering extends TemplateRendering {

    public MockTemplateRendering() {
        super();
    }

    @Override
    protected void render(
            HttpServletRequest request,
            HttpServletResponse response,
            String path,
            Map<String, Object> data) throws Exception {


    }

    @Override
    public boolean shouldHandle(Class<?> targetClass, Method method) {
        return true;
    }

    @Override
    public String getUid() {
        return "mockTemplate";
    }
}
