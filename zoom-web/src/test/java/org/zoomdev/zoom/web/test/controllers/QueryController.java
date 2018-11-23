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

            @Param(pathVariable = true, name = "page")
                    String test
    ) {

    }
    @Mapping(value = "module/{page}/id",method = {Mapping.POST})
    public void pageSegment4(

            @Param(pathVariable = true, name = "page")
                    String test
    ) {

    }

    @Mapping(value = "module/{page}/id",method = {Mapping.GET})
    public void pageSegment5(

            @Param(pathVariable = true, name = "page")
                    String test
    ) {

    }

    @Mapping(value = "module/{page}/id",method = {Mapping.DELETE})
    public void pageSegment6(

            @Param(pathVariable = true, name = "page")
                    String test
    ) {

    }

    @Mapping("module/{page}/{id}")
    public void pageSegment2(
            @Param(pathVariable = true)
                    String page,
            @Param(pathVariable = true)
                    Integer id

    ) {

    }

    @Mapping("{module}/index/{id}")
    public void pageIndex(
            @Param(pathVariable = true)
                    String module,
            @Param(pathVariable = true)
                    Integer id,
            String param0
    ) {

    }


    @Mapping("{module}/{page}/{id}")
    public void page(

            @Param(pathVariable = true)
                    String module,
            @Param(pathVariable = true)
                    String page,
            @Param(pathVariable = true)
                    String id,
            String param0
    ) {

    }

}
