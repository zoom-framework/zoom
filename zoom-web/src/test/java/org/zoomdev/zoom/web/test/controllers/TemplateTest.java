package org.zoomdev.zoom.web.test.controllers;


import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.view.impl.JsonView;
import org.zoomdev.zoom.web.view.impl.RedirectView;

import java.util.HashMap;
import java.util.Map;

@Controller(key="temp")
public class TemplateTest {

    public void index(){
    }


    public String test(){
        return "temp/test";
    }

    public Map<String,Object> hello(ActionContext context){
        context.put("id","1");
        context.put("data","title");
        return new HashMap<String, Object>();
    }


    public void testError(){
        throw new RuntimeException();
    }


    public String test1(){
        return "temp/test1.html";
    }


    public JsonView testView(){
        return new JsonView(new HashMap<String,Object>());
    }



    public RedirectView redirectView(){
        return new RedirectView("redirect/test");
    }


    public String redirectView2(){
        return "redirect:redirect/view2";
    }
}
