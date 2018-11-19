package org.zoomdev.zoom.web.test.controllers;

import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.action.Monitor;
import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.annotations.JsonResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

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
            LoginRequest request
    ) {


    }
}
