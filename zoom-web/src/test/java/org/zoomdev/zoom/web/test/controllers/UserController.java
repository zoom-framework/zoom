package org.zoomdev.zoom.web.test.controllers;

import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.action.Monitor;
import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.annotations.Mapping;
import org.zoomdev.zoom.web.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

@Controller(key = "user")
public class UserController {

    @Inject
    Monitor monitor;

    /**
     * simple
     */
    @JsonResponse
    public void index() {

    }

    /**
     * Simple type post test
     *
     * @param id
     */
    @JsonResponse
    public void put(
            Integer id,
            Date date,
            String name,
            short age
    ) {
    }


    @JsonResponse
    public void basic(HttpServletResponse response, HttpServletRequest request,
                      ActionContext context,
                      HttpSession session) {
    }

    public static class LoginRequest {

        private String account;
        private String password;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @JsonResponse
    public void login(
            @Param(body = true)
            LoginRequest request
    ) {


    }

    public static class HeaderInfo{

        String img;
        String info;
        int age;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }


    /// update the head info
    @JsonResponse
    public void update(
            HeaderInfo head,
            String id
    ){

    }

    /// update the head info
    @JsonResponse
    public void updateHead(
            Map<String,Object> head,
            String id
    ){

        System.out.println(head);
    }


}
