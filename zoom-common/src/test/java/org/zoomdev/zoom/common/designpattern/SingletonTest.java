package org.zoomdev.zoom.common.designpattern;

import junit.framework.TestCase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonTest extends TestCase {

    private Map<String, Object> data = new ConcurrentHashMap<String, Object>();


    private Object aValueNeedsToBeSynchronized;

    public void test() {

        SingletonUtils.doubleLockMap(data, "id", new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return "123";
            }
        });


        assertEquals(data.get("id"), "123");

        SingletonUtils.modify(data, "id2", new SingletonUtils.SingletonModify<Object>() {
            @Override
            public Object modify(Object o) {
                return "";
            }

            @Override
            public Object create() {
                return "create";
            }
        });

        assertEquals(data.get("id2"), "create");

        SingletonUtils.modify(data, "id2", new SingletonUtils.SingletonModify<Object>() {
            @Override
            public Object modify(Object o) {
                return "modified object";
            }

            @Override
            public Object create() {
                return "";
            }
        });

        assertEquals(data.get("id2"), "modified object");

        SingletonUtils.doubleLockValue(this, new SingletonUtils.SingletonValue<Object>() {
            @Override
            public Object getValue() {
                return aValueNeedsToBeSynchronized;
            }

            @Override
            public void setValue(Object o) {
                aValueNeedsToBeSynchronized = o;
            }
        }, new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return "created value";
            }
        });

        assertEquals(aValueNeedsToBeSynchronized, "created value");

    }

}
