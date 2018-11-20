package org.zoomdev.zoom.web.test.controllers;


import org.zoomdev.zoom.web.annotations.Controller;
import org.zoomdev.zoom.web.annotations.JsonResponse;
import org.zoomdev.zoom.web.annotations.Mapping;
import org.zoomdev.zoom.web.annotations.Param;

@JsonResponse
@Controller
public class QueryController {




    @Mapping("module/{page}/id")
    public void pageSegment3(

            @Param(pathVariable = true,name = "page")
                    String test
    ){

    }

    @Mapping("module/{page}/{id}")
    public void pageSegment2(){

    }

    @Mapping("{module}/index/{id}")
    public void pageIndex(){

    }


    @Mapping("{module}/{page}/{id}")
    public void page(){

    }

}
