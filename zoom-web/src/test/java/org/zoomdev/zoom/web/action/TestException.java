package org.zoomdev.zoom.web.action;

import junit.framework.TestCase;
import org.zoomdev.zoom.web.exception.StatusException;

public class TestException extends TestCase {

    public void test() {

        StatusException exception = new StatusException(
                500
        );

        assertEquals(exception.getStatus(), 500);

        exception = new StatusException(
                404,
                "error404"
        );
        assertEquals(exception.getStatus(), 404);
        assertEquals(exception.getCode(), "error404");
        assertEquals(exception.getMessage(), "[404]: error404");


        exception = new StatusException(
                404,
                "error404",
                "Not found"
        );
        assertEquals(exception.getStatus(), 404);
        assertEquals(exception.getCode(), "error404");
        assertEquals(exception.getMessage(), "Not found");

        assertEquals(exception.getError(), "Not found");

        exception.setError("test");
        assertEquals(exception.getMessage(), "test");

        assertEquals(exception.getError(), "test");

        exception.setCode("code");
        assertEquals(exception.getCode(), "code");

        exception.setStatus(1);
        assertEquals(exception.getStatus(), 1);


        StatusException.ApiError apiError = new StatusException.ApiError("太多了");
        assertEquals(apiError.getStatus(), 418);

        assertEquals(apiError.getError(), "太多了");


    }
}
