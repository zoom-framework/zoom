package org.zoomdev.zoom.common.utils;

import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.filter.Filter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/// 在jdk 1.8 有替代，本类是为了适配jdk1.6存在
public class CollectionUtils {

    public static Map<String, Object> asMap(Object... values) {
        return MapUtils.asMap(values);
    }

    public static <T> Set<T> asSet(T... values) {
        Set<T> set = new LinkedHashSet<T>();
        for (T string : values) {
            set.add(string);
        }
        return set;
    }



    public static <T> List<T> filter(T[] array, Filter<T> filter) {
        assert(array!=null);
        List<T> result = new ArrayList<T>(array.length);
        for(T data : array){
            if(filter.accept(data)){
                result.add(data);
            }
        }
        return result;
    }

    public static <T> void visit(Iterable<T> list, Visitor<T> visitor) {

        for(T data : list){
            visitor.visit(data);
        }

    }


    static interface KeyValue {
        String getKeyValue(Object data, String... keys);
    }

    private static class ClassAndKeys {

        String[] tables;
        Class<?> type;

        int h;

        public ClassAndKeys(Class<?> type, String... tables) {
            this.type = type;
            this.tables = tables;
        }

        @Override
        public int hashCode() {
            int h = this.h;
            if (h == 0) {
                if (type != null) {
                    h = 31 + type.hashCode();
                }
                for (String table : tables) {
                    h = 31 * h + table.hashCode();
                }
                this.h = h;
            }
            return h;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClassAndKeys) {
                ClassAndKeys key = (ClassAndKeys) obj;
                if (key.type != this.type) {
                    return false;
                }

                return Arrays.equals(key.tables, this.tables);

            }
            return false;
        }
    }

    static Map<ClassAndKeys, KeyValue> pool = new ConcurrentHashMap<ClassAndKeys, KeyValue>();

    static class KeyValueImpl implements KeyValue {

        private Field[] fields;

        KeyValueImpl(Class<?> type, String... keys) {

            Field[] fields = new Field[keys.length];
            for (int i = 0; i < keys.length; ++i) {
                fields[i] = Classes.getField(type, keys[i]);
                fields[i].setAccessible(true);
            }

            this.fields = fields;

        }

        @Override
        public String getKeyValue(Object data, String... keys) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Field field : fields) {
                try {
                    if (first)
                        first = false;
                    else
                        sb.append(",");
                    sb.append(field.get(data));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return sb.toString();
        }
    }

    public static <T> Map<String, T> toMap( List<T> list, final String... keys) {

        if(list.size() == 0){
            return Collections.emptyMap();
        }

        final Class<T> classOfT = (Class<T>) list.get(0).getClass();

        KeyValue value = SingletonUtils.doubleLockMap(pool,
                new ClassAndKeys(classOfT, keys),
                new SingletonUtils.SingletonInit<KeyValue>() {
                    @Override
                    public KeyValue create() {
                        return new KeyValueImpl(classOfT,keys);
                    }
                }
        );

        Map<String, T> map = new HashMap<String, T>();
        for (T data : list) {
            map.put(value.getKeyValue(data, keys), data);
        }
        return map;
    }

    /**
     * valueMap(true,"id","name") => Map("id":true,"name":true)
     *
     * @param value
     * @param keys
     * @return
     */
    public static Map<String, Object> valueMap(Object value, String... keys) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (String string : keys) {
            map.put(string, value);
        }
        return map;
    }

    public static String[] toArray(List<String> values) {
        return values.toArray(new String[values.size()]);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    /**
     * 遍历array,可能转化array类型
     *
     * @param it
     * @param converter
     * @param <T>
     * @param <E>
     * @return
     */
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

    public static <T, E> List<E> map(Collection<T> it, Converter<T, E> converter) {
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

    public static <T, E> Set<E> newSet(Iterable<T> iterable, Convert<T, E> convert) {

        Set<E> set = new LinkedHashSet<E>();

        for (T data : iterable) {
            set.add(convert.convert(data));
        }
        return set;
    }
}
