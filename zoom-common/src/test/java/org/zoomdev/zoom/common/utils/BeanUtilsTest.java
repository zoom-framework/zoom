package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class BeanUtilsTest extends TestCase {

    public static class A {

        private String id;
        private String name;
        private Double price;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }

    public static class B {

    }

    public void test1() {

        A a = new A();
        a.setId("1");
        a.setName("a");
        a.setPrice(100D);

        A b = new A();
        b.setId("2");
        b.setName("b");

        A merged = BeanUtils.merge(a, b);

        assertTrue(merged == a);

        assertEquals(
                merged.getId(), "2"

        );

        assertEquals(merged.getName(), "b");

        assertEquals(merged.getPrice(), 100D);

    }

    public void test2(){

        List<A> list = new ArrayList<A>();

        A a = new A();
        a.setId("1");
        a.setName("a");
        a.setPrice(100D);

        A b = new A();
        b.setId("2");
        b.setName("b");

        list.add(a);
        list.add(b);

        A c = new A();
        c.setId("1");
        c.setPrice(200D);

        A d = new A();
        d.setId("2");
        d.setPrice(300D);

        A e = new A();
        e.setId("3");
        e.setPrice(400D);

        List<A> list2 = new ArrayList<A>();
        list2.add(c);
        list2.add(d);
        list2.add(e);

        List<A> merged = BeanUtils.mergeList(list,list2,"id");

        assertEquals(merged.size(),3);

        A m = merged.get(0);
        assertEquals(m.getPrice(),200D);

        m = merged.get(1);
        assertEquals(m.getPrice(),300D);

        m = merged.get(2);
        assertEquals(m.getPrice(),400D);

    }
}
