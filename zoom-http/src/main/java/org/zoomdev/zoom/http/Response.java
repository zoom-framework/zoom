package org.zoomdev.zoom.http;

import org.zoomdev.zoom.common.io.Io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Response {

    private final HttpURLConnection connection;
    private final Request request;


    public Response(HttpURLConnection connection, Request request) {
        this.connection = connection;
        this.request = request;
    }

    public String string() throws IOException {
        try {

            return Io.readString(getInputStream(), request.encoding() == null ? "UTF-8" : request.encoding());
        } finally {
            close();
        }
    }

    private InputStream getInputStream() {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            return connection.getErrorStream();
        }
    }

    public byte[] bytes() throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Io.copyAndClose(getInputStream(), byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } finally {
            close();
        }

    }

    public void close() {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Throwable e) {
            }
        }
    }

    public int getStatusCode() throws IOException {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            close();
            throw e;
        }
    }

}
