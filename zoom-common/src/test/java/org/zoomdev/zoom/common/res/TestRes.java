package org.zoomdev.zoom.common.res;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.filter.Filter;

import java.io.IOException;

public class TestRes extends TestCase {

    /**
     * 测试扫描本地
     * @throws IOException
     */
    public void test() throws IOException {

        ResScanner scanner = new ResScanner();
        scanner.scan();


        assertNotNull( scanner.getFile("application.json"));

        assertNotNull(scanner.getClass("org.zoomdev.zoom.common.res.TestRes"));

        scanner.findFile("*.json");
        scanner.findFile(new Filter<ResScanner.Res>() {
            @Override
            public boolean accept(ResScanner.Res value) {
                return true;
            }
        });

        scanner.findClass("*.Test*");
        scanner.findClass(new Filter<String>() {
            @Override
            public boolean accept(String value) {
                return true;
            }
        });


        scanner.destroy();
    }



    public void testSanJar(){

    }


    public void testScanInputStream(){


    }
}
