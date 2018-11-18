package org.zoomdev.zoom.web.test.controllers;

import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.annotations.JsonResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller(key="user")
public class UserController {


    @JsonResponse
    public void index(String str){
        System.out.println("visit user");
    }
}
