package org.zoomdev.zoom.web.res;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.res.ResScanner;

import java.io.IOException;

public class TestRes extends TestCase {
    /**
     * @throws IOException
     */
    public void testScanInputStream() throws IOException {

        ResScanner scanner = new ResScanner();
        scanner.scan();


    }
}
