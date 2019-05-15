package org.zoomdev.zoom.http;

import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.utils.MapUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String,String> headers() {
        Map<String, List<String>> data = connection.getHeaderFields();
        Map<String,String> headers = new HashMap<String, String>();
        for(Map.Entry<String,List<String>> entry : data.entrySet()){
            List<String> list = entry.getValue();
            if(list.size()>0){
                headers.put(entry.getKey(),list.get(0));
            }

        }
        return headers;
    }
}
