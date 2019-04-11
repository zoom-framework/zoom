package org.zoomdev.zoom.common.res;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.utils.Visitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TestRes extends TestCase {

    /**
     * 测试扫描本地
     *
     * @throws IOException
     */
    public void test() throws IOException {

        ResScanner scanner = new ResScanner();
        assertEquals(scanner.getScanFilter(), null);
        scanner.scan();


        assertNotNull(scanner.getFile("application.json"));

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


        ResScanner.ClassRes res = scanner.getClass("org.zoomdev.zoom.common.res.TestRes");
        assertEquals(res.getType(), TestRes.class);


        assertNotNull(res.getFields());

        assertNotNull(res.getPubMethods());

        assertNotNull(scanner.getClass("org.zoomdev.plugin.demo.controllers.DemoController"));


        scanner.visitClass(new Visitor<ResScanner.ClassRes>() {
            @Override
            public void visit(ResScanner.ClassRes data) {

            }
        });


        List<ResScanner.Res> jar = scanner.findFile("*demo*.jar");
        assertTrue(jar.size() > 0);

        File file = jar.get(0).getFile();
        assertNotNull(file);

        ResScanner scanner1 = new ResScanner();
        FileInputStream is = new FileInputStream(file);
        scanner1.scan(is, getClass().getClassLoader());


        scanner.destroy();
    }


    public void testResLoader() {
        File file = ResLoader.getResourceAsFile("application.json");
        assertTrue(file != null && file.exists());

        file = ResLoader.getResourceAsFile("test/test.json");
        assertTrue(file != null && file.exists());

        InputStream is = ResLoader.getResourceAsStream("application.json");

    }


}
