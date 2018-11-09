package com.jzoom.zoom.web.view.impl;

import com.jzoom.zoom.common.io.Io;
import com.jzoom.zoom.web.view.View;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

public abstract class OutputStreamView implements View {



    public OutputStreamView(){
    }

    @Override
    public void render(HttpServletResponse response) throws Exception {
        OutputStream os = null;
        try{
            response.setHeader("Content-Disposition", new StringBuilder().append( "attachment; filename=")
                    .append(URLEncoder.encode(getName(), response.getCharacterEncoding())).toString() );
            os = response.getOutputStream();
            writeTo(os);
        } finally {
            try {
                close();
            } catch (Throwable t) {

            }
            Io.close(os);
        }

    }

    protected abstract void close() throws IOException;

    protected abstract String getName();

    protected abstract void writeTo(OutputStream outputStream) throws IOException;
}
