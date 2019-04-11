package org.zoomdev.zoom.common.config;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.res.ResScanner;

import java.io.File;
import java.io.IOException;

public class TestConfigReader extends TestCase {


    public void test() throws IOException {

        ConfigReader reader = ConfigReader.getDefault();

        ResScanner scanner = new ResScanner();
        scanner.scan();

        ResScanner.Res appconfig = scanner.getFile("application.properties");
        assertNotNull(appconfig);

        File file = appconfig.getFile();

        assertTrue(file.exists());


        reader.load(file);

        assertNotNull(reader.get("ip"));
    }

    public void test2() throws IOException {

        ConfigReader reader = ConfigReader.getDefault();

        ResScanner scanner = new ResScanner();
        scanner.scan();

        ResScanner.Res appconfig = scanner.getFile("application.json");
        assertNotNull(appconfig);

        File file = appconfig.getFile();

        assertTrue(file.exists());


        reader.load(file);

        assertNotNull(reader.get("ip"));
    }
}
