package org.zoomdev.zoom.web.action;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

public class Monitor {

    private AtomicInteger num = new AtomicInteger(0);

    private Object[] arguments;
    private HttpServletResponse response;

    public void increase() {
        num.incrementAndGet();
    }

    public void setArguments(HttpServletResponse response,Object... arguments) {
        this.arguments = arguments.clone();
        num.incrementAndGet();
        this.response = response;
    }

    public Object[] arguments() {
        return arguments;
    }

    public int count() {
        return num.get();
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
