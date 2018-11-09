package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;

public class BeanUtilsTest extends TestCase {


    static class A{
        private String a;
        private int b;
        private float c;
        private boolean d;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public float getC() {
            return c;
        }

        public void setC(float c) {
            this.c = c;
        }

        public boolean isD() {
            return d;
        }

        public void setD(boolean d) {
            this.d = d;
        }


    }


    public void testSimpleClass(){


    }
}
