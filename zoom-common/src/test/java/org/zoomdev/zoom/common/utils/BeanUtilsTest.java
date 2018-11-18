package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;

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

    public void testSimpleClass() {

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

    public void testList(){

        

    }
}
