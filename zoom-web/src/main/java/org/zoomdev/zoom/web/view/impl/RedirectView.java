package org.zoomdev.zoom.web.view.impl;

import org.zoomdev.zoom.web.view.View;

import javax.servlet.http.HttpServletResponse;

public class RedirectView implements View {

    private String url;

    public RedirectView(String url) {
        this.url = url;
    }

    @Override
    public void render(HttpServletResponse response) throws Exception {
        response.sendRedirect(url);
    }

}
