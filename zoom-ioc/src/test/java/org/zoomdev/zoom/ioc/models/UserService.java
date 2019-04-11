package org.zoomdev.zoom.ioc.models;

import org.zoomdev.zoom.http.annotations.Inject;

public class UserService {

    @Inject(config = "name")
    private String name;


    public String getAdmin() {
        return name;
    }


}
