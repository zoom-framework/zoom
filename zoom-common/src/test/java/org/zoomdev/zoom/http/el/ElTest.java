package org.zoomdev.zoom.http.el;

import junit.framework.TestCase;
import org.zoomdev.zoom.http.config.ConfigReader;
import org.zoomdev.zoom.http.res.ResScanner;

import java.io.IOException;

public class ElTest extends TestCase {

    public void test() throws IOException {
        ResScanner scanner = new ResScanner();
        scanner.scan(getClass().getClassLoader());
        ResScanner.Res res = scanner.getFile("application.properties");

        ConfigReader reader = ConfigReader.getDefault();
        reader.load(res.getFile());

        assertEquals(ConfigReader.parseValue("${zoom.env}"), "test");

        assertEquals(ConfigReader.parseValue("zoom.env"), "zoom.env");
    }
}
