package org.zoomdev.zoom.web.action;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.CollectionUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class MockHttpServletResponse implements HttpServletResponse {


    public String getContent() {
        if (writer != null) {
            return stringWriter.toString();
        }

        return new String(outputStream.getBytes());

    }


    public byte[] getBytes() {
        if (outputStream != null) {
            return outputStream.getBytes();
        }
        return stringWriter.toString().getBytes();

    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {
        this.redirectUrl = location;
    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {


    }

    private Map<String, List<String>> header = new HashMap<String, List<String>>();

    @Override
    public void setHeader(String name, String value) {
        header.put(name, CollectionUtils.asList(value));
    }

    @Override
    public void addHeader(String name, String value) {
        if (header.containsKey(name)) {
            ((List<String>) header.get(name)).add(value);
        } else {
            header.put(name, CollectionUtils.asList(value));
        }
    }

    @Override
    public void setIntHeader(String name, int value) {

    }

    @Override
    public void addIntHeader(String name, int value) {

    }

    private int status;
    private String message;

    @Override
    public void setStatus(int sc) {
        status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.status = sc;
        this.message = sm;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String name) {
        return CollectionUtils.getAt(header.get(name), 0);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return header.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return header.keySet();
    }

    private String encoding = "utf-8";

    @Override
    public String getCharacterEncoding() {
        return encoding;
    }

    @Override
    public String getContentType() {
        return null;
    }

    private MockServletOutputStream outputStream;

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new MockServletOutputStream();
        } else {
            throw new ZoomException("Stream is already opened");
        }
        return outputStream;
    }

    private PrintWriter writer;
    private StringWriter stringWriter = new StringWriter();

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(stringWriter);
        } else {
            if (outputStream != null)
                throw new ZoomException("Stream is already opened");
        }
        return writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.encoding = charset;
    }

    @Override
    public void setContentLength(int len) {
        header.put("Content-Length", CollectionUtils.asList(String.valueOf(len)));
    }


    @Override
    public void setContentType(String type) {
        header.put("Content-Type", CollectionUtils.asList(type));
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    private String redirectUrl;

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
