package org.zoomdev.zoom.common.utils;

import org.zoomdev.zoom.common.exceptions.ZoomException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MapUtils {

    /**
     * 方便的创建一个Map,使用方法: MapUtils.asMap(key0,value0,key1,value1......)
     *
     * @param values
     * @return
     */
    public static Map<String, Object> asMap(Object... values) {

        if (values.length % 2 != 0) {
            throw new ZoomException("asMap的参数个数必须为2的倍数");
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        for (int i = 0, c = values.length; i < c; i += 2) {
            data.put((String) values[i], values[i + 1]);
        }

        return data;

    }

    /**
     * 将一个list转成id指定的key对应的map
     *
     * @param list
     * @param id
     * @return
     */
    public static Map<String, Map<?, ?>> toMap(List<? extends Map<String, ?>> list, Object id) {
        Map<String, Map<?, ?>> result = new HashMap<String, Map<?, ?>>(list.size());
        for (Map<String, ?> map : list) {
            result.put((String) map.get(id), map);
        }

        return result;
    }


    /**
     * 将列表转化为map
     *
     * @param list
     * @param keyExtra
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> toMap(List<T> list, Converter<T, String> keyExtra) {
        Map<String, T> result = new HashMap<String, T>();
        for (T data : list) {
            result.put(keyExtra.convert(data), data);
        }

        return result;
    }


    public static Map<String, String> toKeyAndLabel(List<? extends Map<String, ?>> list, Object id, Object label) {
        Map<String, String> result = new HashMap<String, String>(list.size());
        for (Map<String, ?> map : list) {
            result.put(String.valueOf(map.get(id)), (String) map.get(label));
        }

        return result;
    }

    /**
     * 转化Map为其他类型
     *
     * @param src
     * @param converter
     * @param <VT>
     * @param <VV>
     * @return
     */
    public static <VT, VV> Map<String, VV> convert(Map<String, VT> src, Converter<VT, VV> converter) {
        assert (src != null);
        Map<String, VV> dest = null;
        try {
            dest = src.getClass().newInstance();
        } catch (Exception e) {
            throw new ZoomException("不能初始化要求转化的Map类型" + src.getClass(), e);
        }
        for (Map.Entry<String, VT> entry : src.entrySet()) {
            dest.put(entry.getKey(), converter.convert(entry.getValue()));
        }
        return dest;
    }


    /**
     * 转成 a=b&c=d的形式
     *
     * @param data
     * @return
     */
    public static String toQueryString(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=");
            if (entry.getValue() == null) {
                continue;
            }
            sb.append(String.valueOf(entry.getValue()));
        }

        return sb.toString();

    }
}
