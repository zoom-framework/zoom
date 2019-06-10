package org.zoomdev.zoom.web.test.controllers;

import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.exception.StatusException;

@Controller(key="test")
public class TestOtherController {

    // 500
    @JsonResponse
    public void testStatusException500(){
        throw new ZoomException();
    }

    // 404
    @JsonResponse
    public void testStatusException404(){
        throw new StatusException.NotFoundException();
    }


    @JsonResponse
    public void testStatusException403(){
        throw new StatusException.AuthException();
    }

    @JsonResponse
    public void testStatusException401(){
        throw new StatusException.UnAuthException();
    }

    // 404
    @JsonResponse
    public void testStatusException400(){
        throw new StatusException.BadRequest();
    }

    // 404
    @JsonResponse
    public void testStatusException405(){
        throw new StatusException.NotAllowedHttpMethodException("POST");
    }

    // 404
    @JsonResponse
    public void testStatusException500_(){
        throw new StatusException.ServerException();
    }
}
