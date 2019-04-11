package org.zoomdev.zoom.http.designpattern;

import org.zoomdev.zoom.http.lock.LockUtils;

import java.util.Map;

public class SingletonUtils {

    public static interface SingletonInit<V> {
        V create();
    }

    public static interface SingletonModify<V> extends SingletonInit<V> {
        V modify(V v);
    }


    public static interface SingletonValue<V> {
        V getValue();

        void setValue(V v);
    }


    /**
     * 检查map中存不存在key，如果不存在，那么创建新的值，并存入map
     *
     * @param map
     * @param key
     * @param init
     * @return
     */
    public static <K, V> V doubleLockMap(Map<K, V> map, K key, SingletonInit<V> init) {
        V value = map.get(key);
        if (value == null) {
            synchronized (map) {
                value = map.get(key);
                if (value == null) {
                    value = init.create();
                    map.put(key, value);
                }
            }
        }

        return value;

    }

    /**
     * 判断某个值存不存在，如果不存在则同步创建
     *
     * @param lock
     * @param lockValue
     * @param init
     * @param <V>
     * @return
     */
    public static <V> V doubleLockValue(Object lock, SingletonValue<V> lockValue, SingletonInit<V> init) {
        V value = lockValue.getValue();
        if (value == null) {
            synchronized (lock) {
                value = lockValue.getValue();
                if (value == null) {
                    value = init.create();
                    lockValue.setValue(value);
                }
            }
        }
        return value;

    }

    /**
     * 这里锁的问题通过key来分散，同一个hashcode对应的key才会锁住
     *
     * @param map
     * @param key
     * @param init
     * @return
     */
    public static <K, V> V liteDoubleLockMap(Map<K, V> map, K key, SingletonInit<V> init) {
        V value = map.get(key);
        if (value == null) {
            synchronized (LockUtils.getLock(key)) {
                value = map.get(key);
                if (value == null) {
                    value = init.create();
                    map.put(key, value);
                }
            }
        }

        return value;

    }


    /**
     * 同步修改一个map中的内容，对于同一个key，判断不存在则创建，否则修改。
     * 创建和修改都是线程安全的。
     *
     * @param map
     * @param key
     * @param modify
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> V modify(Map<K, V> map, K key, SingletonModify<V> modify) {

        V value = map.get(key);
        if (value == null) {
            synchronized (LockUtils.getLock(key)) {
                value = map.get(key);
                if (value == null) {
                    value = modify.create();
                    map.put(key, value);
                }
            }
        } else {
            V oldValue;
            synchronized (LockUtils.getLock(key)) {
                oldValue = modify.modify(value);
            }
            if (oldValue != value) {
                map.put(key, modify.modify(value));
            }
        }

        return value;
    }

}
