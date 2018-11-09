package com.jzoom.zoom.common.res;

import junit.framework.TestCase;

import java.io.IOException;

public class TestRes extends TestCase {

    public void test() throws IOException {
        ResScanner scanner = ResScanner.me();

        scanner.scan(getClass().getClassLoader());
    }
}
