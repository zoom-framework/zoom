package org.zoomdev.zoom.web.view.impl;

import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.web.utils.ResponseUtils;
import org.zoomdev.zoom.web.view.View;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;


/**
 * 渲染文件
 *
 * @author jzoom
 */
public class FileView implements View {

    private File file;
    private String name;

    public FileView(File file) {
        this(file, file.getName());
    }

    public FileView(File file, String name) {
        assert (file != null && name != null);
        this.file = file;
        this.name = name;
    }

    @Override
    public void render(HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", new StringBuilder().append("attachment; filename=")
                .append(URLEncoder.encode(name, response.getCharacterEncoding())).toString());
        FileInputStream inputStream = new FileInputStream(file);
        int length = inputStream.available();
        response.setHeader("Content-Length", String.valueOf(length));
        try {
            ResponseUtils.write(response, inputStream, length);
        } finally {
            Io.close(inputStream);
        }

    }

}
