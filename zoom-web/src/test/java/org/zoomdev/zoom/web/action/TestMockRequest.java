package org.zoomdev.zoom.web.action;

import com.sun.prism.impl.Disposer;
import junit.framework.TestCase;
import org.apache.commons.lang3.ObjectUtils;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.DataObject;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMockRequest extends TestCase {

    public void test() throws IOException {

        HttpServletRequest request = mockJsonRequest(new DataObject()
        .set("id","1").set("name","123"));

        String str = Io.readString(request.getInputStream(),"utf-8");
        System.out.println(str);

    }

    private HttpServletRequest mockJsonRequest(
            Object data
    ) throws IOException {

        ServletInputStream inputStream = new MockServletInputStream(JSON.stringify(
                data
        ).getBytes());

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn("/user/index");
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("Content-Type")).thenReturn("application/json");
        when(request.getInputStream()).thenReturn(inputStream);




        return request;
    }

}
