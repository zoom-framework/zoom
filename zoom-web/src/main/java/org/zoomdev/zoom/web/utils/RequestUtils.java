package org.zoomdev.zoom.web.utils;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.utils.CachedClasses;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RequestUtils {

    /**
     * 取出request中的所有Attribute
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getAttributes(HttpServletRequest request) {
        assert (request != null);
        Map<String, Object> data = new HashMap<String, Object>();
        merge(data, request);
        return data;
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






}
