package org.zoomdev.zoom.web.action;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.utils.DataObject;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.ZoomFilter;
import org.zoomdev.zoom.web.test.controllers.UserController;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionTest extends TestCase {

    ZoomFilter filter = new ZoomFilter();

    public void test() throws ServletException, IOException {
        FilterConfig config = mock(FilterConfig.class);
        when(config.getInitParameter("exclusions")).thenReturn("*.js|*.gif|*.jpg|*.png|*.css|*.ico|*.jar");


        IocContainer ioc = filter.getWeb().getIoc();
        Monitor monitor = new Monitor();
        ioc.getIocClassLoader().append(Monitor.class, monitor, false);

        filter.init(config);

        int count = 0;

//        request("/user/index", DataObject.as());
//
//        assertEquals(monitor.count(), ++count);
//
//        request("/user/put", DataObject.as(
//                "id", "1",  //passs different type
//                "age", 20,
//                "date", "2017-08-12",
//                "name", "韩贝贝"
//        ));
//        assertEquals(monitor.count(), ++count);
//        assertEquals(monitor.arguments()[0], 1);
//
//        request("/user/basic", DataObject.as(
//
//        ));
//        assertEquals(monitor.count(), ++count);
//        assertTrue(HttpServletResponse.class.isAssignableFrom(monitor.arguments()[0].getClass()));
//        assertTrue(HttpServletRequest.class.isAssignableFrom(monitor.arguments()[1].getClass()));
//        assertTrue(ActionContext.class.isAssignableFrom(monitor.arguments()[2].getClass()));
//        assertTrue(HttpSession.class.isAssignableFrom(monitor.arguments()[3].getClass()));

        request("/user/login", DataObject.as(
            "account","user",
                "password","password"
        ));
        assertEquals(monitor.count(), ++count);
        assertTrue(UserController.LoginRequest.class.isAssignableFrom(monitor.arguments()[0].getClass()));
        UserController.LoginRequest request = (UserController.LoginRequest) monitor.arguments()[0];
        assertEquals(request.getAccount(),"user");
        assertEquals(request.getPassword(),"password");



        request("/user/update", DataObject.as(
                "head",DataObject.as(
                        "img","image_url",
                        "info","name",
                        "age","15"
                ),
                "id",1
        ));
        assertEquals(monitor.count(), ++count);
        assertTrue(UserController.HeaderInfo.class.isAssignableFrom(monitor.arguments()[0].getClass()));
        UserController.HeaderInfo headerInfo = (UserController.HeaderInfo) monitor.arguments()[0];
        assertEquals(headerInfo.getImg(),"image_url");
        assertEquals(headerInfo.getAge(),15);
        assertEquals(monitor.arguments()[1],"1");
        ///test url

        request("/user/updateHead", DataObject.as(
                "head",DataObject.as(
                        "img","image_url",
                        "info","name",
                        "age","15"
                ),
                "id",1
        ));
        assertEquals(monitor.count(), ++count);
        assertTrue(Map.class.isAssignableFrom(monitor.arguments()[0].getClass()));

        assertEquals(monitor.arguments()[0],DataObject.as(
                "img","image_url",
                "info","name",
                "age","15"
        ));


        assertEquals(monitor.arguments()[1],"1");

        request("/module/add/id", DataObject.as(
               "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"add" );

        filter.destroy();

    }

    private void request(String url, Object data) throws IOException, ServletException {

        filter.doFilter(mockJsonRequest(
                url,
                data
        ), mockJsonResponse(), mockNextChain());
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


    private HttpServletRequest mockJsonRequest(
            String url,
            Object data
    ) throws IOException {


        return MockHttpServletRequest.json(
                url,
                "POST",
                data,
                new HashMap<String, String>()
        );
    }

}
