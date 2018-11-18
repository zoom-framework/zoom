package org.zoomdev.zoom.web.action;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MockServletInputStream extends ServletInputStream {
    ByteArrayInputStream is;

    public MockServletInputStream(byte[] bytes){
        is = new ByteArrayInputStream(bytes);
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }


}
