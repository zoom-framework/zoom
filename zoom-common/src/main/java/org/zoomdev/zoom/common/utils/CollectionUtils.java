package org.zoomdev.zoom.common.utils;

import java.util.*;

public class CollectionUtils {

    public static Map<String, Object> asMap(Object... values) {
		return MapUtils.asMap(values);
	}
	
	public static <T> Set<T> asSet(T...values){
		Set<T> set = new LinkedHashSet<T>();
		for (T string : values) {
			set.add(string);
		}
		return set;
	}
	
	public static Map<String, Object> valueMap(Object value,String...keys){
		Map<String, Object> map = new LinkedHashMap<String,Object>();
		for (String string : keys) {
			map.put(string, value);
		}
		return map;
	}

    public static String[] toArray(List<String> values) {
        return values.toArray(new String[values.size()]);
    }

    public static interface Converter<T, E> {
        E convert(T data);
    }

    public static <T, E> List<E> map(T[] it, Converter<T, E> converter) {
        List<E> list = new ArrayList<E>(it.length);
        for (T t : it) {
            E result = converter.convert(t);
            //有的时候回返回null，比如做一些过滤
            if (result != null) {
                list.add(result);
            }

        }
        return list;
    }
    public static <T, E> List<E> map(List<T> it, Converter<T, E> converter) {
        List<E> list = new ArrayList<E>(it.size());
        for (T t : it) {
            E result = converter.convert(t);
            //有的时候回返回null，比如做一些过滤
            if (result != null) {
                list.add(result);
            }
        }
        return list;
    }

    public static interface Convert<T, E> {
		E convert(T data);
	}

	public static <T,E> Set<E> newSet(Iterable<T> iterable, Convert<T,E> convert) {

		Set<E> set = new LinkedHashSet<E>();

		for(T data : iterable){
			set.add(convert.convert(data));
		}
		return set;
	}
}
