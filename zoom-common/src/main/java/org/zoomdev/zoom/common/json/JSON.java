package org.zoomdev.zoom.common.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.io.Io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public class JSON {

    static ObjectMapper mapper = new ObjectMapper();

    /**
     * mapper
     *
     * @return
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * 格式化json
     *
     * @param value
     * @return
     */
    public static String stringify(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new ZoomException(e);
        }
    }

    public static void write(OutputStream os, Object value) {
        assert (os!=null);
        try {
            mapper.writeValue(os, value);
        } catch (Exception e) {
            throw new ZoomException(e);
        } finally {
            Io.close(os);
        }
    }

    /**
     * 解析
     *
     * @param src
     * @param classOfT
     * @return
     */
    public static <T> T parse(String src, Class<T> classOfT) {
        assert (src!=null);
        try {
            return mapper.readValue(src, classOfT);
        } catch (Exception e) {
            throw new ZoomException(e);
        }
    }

    public static <T> T parse(String src, TypeReference<T> classOfT) {
        assert (src!=null);
        try {
            return mapper.readValue(src, classOfT);
        } catch (Exception e) {
            throw new ZoomException(e);
        }
    }



    /**
     * @param src
     * @param classOfT
     * @return
     */
    public static <T> T parse(InputStream src, Class<T> classOfT) {
        assert (src!=null);
        try {
            return mapper.readValue(src, classOfT);
        } catch (Exception e) {
            throw new ZoomException(e);
        }
    }

    /**
     * @param src
     * @param classOfT
     * @return
     */
    public static <T> T parse(Reader src, Class<T> classOfT) {
       assert (src!=null);
        try {
            return mapper.readValue(src, classOfT);
        } catch (Exception e) {
            throw new ZoomException("从reader解析json失败", e);
        }
    }
}
