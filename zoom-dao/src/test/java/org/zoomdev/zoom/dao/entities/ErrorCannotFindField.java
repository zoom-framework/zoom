package org.zoomdev.zoom.dao.entities;

import org.zoomdev.zoom.dao.annotations.Join;
import org.zoomdev.zoom.dao.annotations.Link;
import org.zoomdev.zoom.dao.annotations.Table;

@Link({@Join(table = "type", on = "tpId=typeId")})
@Table("product")
public class ErrorCannotFindField {


    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    private String test;
}
