package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;

import java.util.ArrayList;

public class PageTest extends TestCase {


    public void test() {

        Page page = new Page();

        page.setList(new ArrayList());
        page.setPage(1);
        page.setTotal(1000);
        page.setSize(30);


    }
}
