package org.zoomdev.zoom.common.io;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.res.ResScanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestIo extends TestCase {

    public void test() throws IOException {

        ResScanner scanner = ResScanner.me();
        scanner.scan();

        ResScanner.Res file = scanner.getFile("application.properties");
        assertNotNull(file);
        byte[] bytes = new byte[(int) file.getFile().length()];
        InputStream stream = file.getInputStream();
        Io.read(file.getInputStream(),bytes);
        System.out.println(new String(bytes));
        System.out.println(Io.readString(file.getInputStream(),"utf-8"));
        assertEquals(Io.readString(stream,"utf-8"),
                new String(bytes));

        Io.closeAny(stream);

        Io.readString(file.getFile(),"utf-8");

        Io.writeString(new File(file.getFile().getAbsolutePath()+".back"),new String(bytes));
    }
}
