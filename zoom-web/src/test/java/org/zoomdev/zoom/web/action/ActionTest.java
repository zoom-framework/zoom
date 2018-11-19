package org.zoomdev.zoom.web.action;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.utils.DataObject;
import org.zoomdev.zoom.web.ZoomFilter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionTest extends TestCase {


    public void test() throws ServletException, IOException {
        FilterConfig config = mock(FilterConfig.class);
        when(config.getInitParameter("exclusions")).thenReturn("*.js|*.gif|*.jpg|*.png|*.css|*.ico|*.jar");

        ZoomFilter filter = new ZoomFilter();
        filter.init(config);


        filter.doFilter(mockJsonRequest("".getBytes()), mockJsonResponse(), mockNextChain());


        filter.destroy();

    }

    private FilterChain mockNextChain() {
        FilterChain chain = mock(FilterChain.class);

        return chain;
    }

    private HttpServletResponse mockJsonResponse() throws IOException {
        ServletOutputStream outputStream = mock(ServletOutputStream.class);


        HttpServletResponse response = mock(HttpServletResponse.class);

        when(response.getOutputStream()).thenReturn(outputStream);

        return response;
    }

    private HttpServletRequest mockJsonRequest(byte[] content) throws IOException {


        return MockHttpServerRequest.json(
                "/user/index",
                "POST",
                new DataObject().set("id", "123"),
                new HashMap<String, String>()
        );
    }

}
