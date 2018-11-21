package org.zoomdev.zoom.web.action;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


    MockServletOutputStream(){

    }


    public byte[] getBytes(){
        return outputStream.toByteArray();
    }


    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }


    @Override
    public void write(byte[] b) throws IOException {
       outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }
}
