package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.common.utils.Visitor;

import java.util.Arrays;

public class Db {

    public static Dao[] daos = new Dao[100];
    public static int count = 0;

    private static final Object lock = new Object();

    public static void register(Dao dao) {
        synchronized (lock) {
            daos[count++] = dao;
        }
    }

    public static void visit(Visitor<Dao> visitor) {
        Dao[] copyDaos;
        int c;
        synchronized (lock) {
            c = count;
            copyDaos = Arrays.copyOf(daos, count);
        }
        for (int i = 0; i < c; ++i) {
            if (copyDaos[i] != null) {
                visitor.visit(copyDaos[i]);
            }
        }
    }


    public static synchronized void unregister(Dao dao) {
        synchronized (lock) {
            for (int i = 0; i < count; ++i) {
                if (daos[i] == dao) {
                    for (int j = i + 1; j < count; ++j) {
                        daos[j - 1] = daos[j];
                    }
                    break;
                }
            }
            daos[--count] = null;
        }
    }


    /**
     * 对于默认的Dao，可以用这种方式来访问
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> EAr<T> ar(Class<T> type) {
        return daos[0].ar(type);
    }

    public static void release() {
        visit(new Visitor<Dao>() {
            @Override
            public void visit(Dao data) {
                data.release();
            }
        });
    }
}
