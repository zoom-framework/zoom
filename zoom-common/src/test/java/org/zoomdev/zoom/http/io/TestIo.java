package org.zoomdev.zoom.http.io;

import junit.framework.TestCase;
import org.zoomdev.zoom.http.res.ResScanner;

import java.io.*;
import java.util.Arrays;

public class TestIo extends TestCase {

    public void test() throws IOException {

        ResScanner scanner = new ResScanner();
        scanner.scan();

        ResScanner.Res file = scanner.getFile("application.properties");
        assertNotNull(file);
        byte[] bytes = new byte[(int) file.getFile().length()];
        InputStream stream = file.getInputStream();
        Io.read(file.getInputStream(), bytes);
        System.out.println(new String(bytes));
        System.out.println(Io.readString(file.getInputStream(), "utf-8"));
        assertEquals(Io.readString(stream, "utf-8"),
                new String(bytes));


        Io.closeAny(stream);


        assertTrue(Arrays.equals(Io.readBytes(file.getFile()), bytes));

        Io.readString(file.getFile(), "utf-8");

        Io.writeString(new File(file.getFile().getAbsolutePath() + ".back"), new String(bytes));


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ByteArrayInputStream inputStream = new ByteArrayInputStream("hello".getBytes());

        Io.copy(inputStream, outputStream);

        assertTrue(Arrays.equals("hello".getBytes(), outputStream.toByteArray()));


    }
}
