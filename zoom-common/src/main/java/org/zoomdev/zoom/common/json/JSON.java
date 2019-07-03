package org.zoomdev.zoom.common.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.io.Io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;

public class JSON {

    static ObjectMapper mapper = new ObjectMapper();

    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(sdf);
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
        assert (os != null);
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
        assert (src != null);
        try {
            return mapper.readValue(src, classOfT);
        } catch (Exception e) {
            throw new ZoomException(e);
        }
    }

    public static <T> T parse(String src, ParameterizedType targetType) {
        JavaType type = TypeFactory.defaultInstance().constructType(targetType);
        try {
            return mapper.readValue(src, type);
        } catch (IOException e) {
            throw new ZoomException(e);
        }
    }


    /**
     * @param src
     * @param classOfT
     * @return
     */
    public static <T> T parse(InputStream src, Class<T> classOfT) {
        assert (src != null);
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
        assert (src != null);
        try {
            return mapper.readValue(src, classOfT);
        } catch (Exception e) {
            throw new ZoomException("从reader解析json失败", e);
        }
    }
}
