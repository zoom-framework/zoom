package org.zoomdev.zoom.web.parameter.pre.impl;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.PreParameterParser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadPreParamParser implements PreParameterParser {

    @Override
    public Object preParse(ActionContext context) throws Exception {
        return doUpload(context.getRequest());
    }

    private static DiskFileItemFactory factory = new DiskFileItemFactory();

    public static Map<String, Object> doUpload(HttpServletRequest request) {

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        try {
            List<FileItem> list = upload.parseRequest(request);
            Map<String, Object> data = new HashMap<String, Object>();
            for (FileItem item : list) {
                String name = item.getFieldName();
                if (item.isFormField()) {
                    String value = item.getString("UTF-8");
                    data.put(name, value);
                } else {


                    data.put(name, item);
                }
            }
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean shouldParse(String contentType) {
        return contentType != null && contentType.startsWith("multipart/form-data;");
    }

}
