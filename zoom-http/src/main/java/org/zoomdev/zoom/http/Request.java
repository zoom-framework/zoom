package org.zoomdev.zoom.http;

import java.util.HashMap;
import java.util.Map;

public class Request {


    public static final String JSON = "application/json";

    public static final String HTML = "text/html";



    public static int READ_TIMEOUT = 24000;
    public static int CONNECT_TIMEOUT = 12000;
    final byte[] body;
    final String contentType;
    final String method;
    String url;
    Map<String, String> headers;
    int readTimeout = READ_TIMEOUT;
    int connectTimeout = CONNECT_TIMEOUT;
    String encoding;

    public static Request post(String url, String contentType, byte[] body) {
        return new Request("POST", contentType, body)
                .url(url);
    }

    public Request headers(Map<String,String> headers){
        if(this.headers!=null){
            this.headers.putAll(headers);
        }else {
            this.headers = headers;
        }

        return this;
    }



    public static Request get(String url,String contentType){
        return new Request("GET",contentType,null).url(url);
    }

    public String encoding() {
        return encoding;
    }

    public Request encoding(String value) {
        this.encoding = value;
        return this;
    }

    public Request readTimeout(int value) {
        this.readTimeout = value;
        return this;
    }

    public Request connectTimeout(int value) {
        this.connectTimeout = value;
        return this;
    }

    public Request header(String key, String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
        return this;
    }

    public Request url(String url) {
        this.url = url;
        return this;
    }

    public Request(String method, String contentType, byte[] body) {
        this.method = method;
        this.contentType = contentType;
        this.body = body;
    }
}
