package org.zoomdev.zoom.web.res;

import junit.framework.TestCase;
import org.zoomdev.zoom.http.res.ClassResolver;
import org.zoomdev.zoom.web.utils.ClassResolvers;
import org.zoomdev.zoom.http.res.ResScanner;

import java.io.IOException;

public class TestRes extends TestCase {
    /**
     *
     * @throws IOException
     */
    public void testScanInputStream() throws IOException {

        ResScanner scanner = new ResScanner();
        scanner.scan();

        ClassResolvers resolvers = new ClassResolvers(
                new ClassResolver() {
                    @Override
                    public void resolve(ResScanner scanner) {

                    }
                }
        );
        resolvers.visit(scanner);
    }
}
