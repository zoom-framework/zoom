package com.jzoom.zoom.web.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class UploadUtils {
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
}
