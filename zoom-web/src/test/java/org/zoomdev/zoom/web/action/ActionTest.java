package org.zoomdev.zoom.web.action;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.json.JSON;
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

        request("/user/index", DataObject.as());

        assertEquals(monitor.count(), ++count);

        request("/user/put", DataObject.as(
                "id", "1",  //passs different type
                "age", 20,
                "date", "2017-08-12",
                "name", "韩贝贝"
        ));
        assertEquals(monitor.count(), ++count);
        assertEquals(monitor.arguments()[0], 1);

        request("/user/basic", DataObject.as(

        ));
        assertEquals(monitor.count(), ++count);
        assertTrue(HttpServletResponse.class.isAssignableFrom(monitor.arguments()[0].getClass()));
        assertTrue(HttpServletRequest.class.isAssignableFrom(monitor.arguments()[1].getClass()));
        assertTrue(ActionContext.class.isAssignableFrom(monitor.arguments()[2].getClass()));
        assertTrue(HttpSession.class.isAssignableFrom(monitor.arguments()[3].getClass()));

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


        request("/module/add/1", DataObject.as(
                "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"add" );
        assertEquals( monitor.arguments()[1],1 );


        request("/myuser/index/1", DataObject.as(
                "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"myuser" );
        assertEquals( monitor.arguments()[1],1 );


        request("/myuser1/index1/1", DataObject.as(
                "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"myuser1" );
        assertEquals( monitor.arguments()[1],"index1" );
        assertEquals( monitor.arguments()[2],"1" );
        assertEquals( monitor.arguments()[3],"param0" );

        ////////////////////////////////////////////

        ///form
        form("/user/index", DataObject.as());

        assertEquals(monitor.count(), ++count);

        form("/user/put", DataObject.as(
                "id", "1",  //passs different type
                "age", 20,
                "date", "2017-08-12",
                "name", "韩贝贝"
        ));
        assertEquals(monitor.count(), ++count);
        assertEquals(monitor.arguments()[0], 1);

        form("/user/basic", DataObject.as(

        ));
        assertEquals(monitor.count(), ++count);
        assertTrue(HttpServletResponse.class.isAssignableFrom(monitor.arguments()[0].getClass()));
        assertTrue(HttpServletRequest.class.isAssignableFrom(monitor.arguments()[1].getClass()));
        assertTrue(ActionContext.class.isAssignableFrom(monitor.arguments()[2].getClass()));
        assertTrue(HttpSession.class.isAssignableFrom(monitor.arguments()[3].getClass()));

        form("/user/login", DataObject.as(
                "account","user",
                "password","password"
        ));
        assertEquals(monitor.count(), ++count);
        assertTrue(UserController.LoginRequest.class.isAssignableFrom(monitor.arguments()[0].getClass()));
        request = (UserController.LoginRequest) monitor.arguments()[0];
        assertEquals(request.getAccount(),"user");
        assertEquals(request.getPassword(),"password");



        form("/user/update", DataObject.as(
                "head", JSON.stringify(DataObject.as(
                        "img","image_url",
                        "info","name",
                        "age","15"
                )),
                "id",1
        ));
        assertEquals(monitor.count(), ++count);
        assertTrue(UserController.HeaderInfo.class.isAssignableFrom(monitor.arguments()[0].getClass()));
        headerInfo = (UserController.HeaderInfo) monitor.arguments()[0];
        assertEquals(headerInfo.getImg(),"image_url");
        assertEquals(headerInfo.getAge(),15);
        assertEquals(monitor.arguments()[1],"1");
        ///test url

        form("/user/updateHead", DataObject.as(
                "head",JSON.stringify(DataObject.as(
                        "img","image_url",
                        "info","name",
                        "age","15"
                )),
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

        form("/module/add/id", DataObject.as(
                "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"add" );


        form("/module/add/1", DataObject.as(
                "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"add" );
        assertEquals( monitor.arguments()[1],1 );


        form("/myuser/index/1", DataObject.as(
                "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"myuser" );
        assertEquals( monitor.arguments()[1],1 );


        form("/myuser1/index1/1", DataObject.as(
                "param0","param0"
        ));

        assertEquals(monitor.count(), ++count);
        assertEquals( monitor.arguments()[0],"myuser1" );
        assertEquals( monitor.arguments()[1],"index1" );
        assertEquals( monitor.arguments()[2],"1" );
        assertEquals( monitor.arguments()[3],"param0" );

        request("/test/testStatusException500",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        assertEquals(monitor.getResponse().getStatus(),500);
        request("/test/testStatusException404",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        request("/test/testStatusException403",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        request("/test/testStatusException401",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        request("/test/testStatusException400",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        request("/test/testStatusException405",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        request("/test/testStatusException500_",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);





        request("/temp/index",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);

        request("/temp/test",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);


        request("/temp/hello",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);


        request("/temp/testError",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);

        request("/temp/test1",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);

        request("/temp/testView",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);

        request("/temp/redirectView",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        assertEquals(((MockHttpServletResponse)monitor.getResponse())
                .getRedirectUrl(),"redirect/test");

        request("/temp/redirectView2",new HashMap<String,Object>());
        assertEquals(monitor.count(), ++count);
        assertEquals(((MockHttpServletResponse)monitor.getResponse())
                .getRedirectUrl(),"redirect/view2");

        filter.destroy();

    }


    private void form(String url, Map<String,Object> data) throws IOException, ServletException {
        filter.doFilter(mockFormRequest(
                url,
                data
        ), mockJsonResponse(), mockNextChain());
    }

    private void request(String url, Object data) throws IOException, ServletException {

        filter.doFilter(mockJsonRequest(
                url,
                data
        ), new MockHttpServletResponse(), mockNextChain());
    }

    private FilterChain mockNextChain() {
        FilterChain chain = mock(FilterChain.class);

        return chain;
    }

    private HttpServletResponse mockJsonResponse() throws IOException {
        return new MockHttpServletResponse();
    }

    private MockHttpServletRequest mockFormRequest(
            String url,
            Map<String,Object> data
    ){
        return MockHttpServletRequest.form(
                url,
                "POST",
                data,
                new HashMap<String, String>()
        );
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
