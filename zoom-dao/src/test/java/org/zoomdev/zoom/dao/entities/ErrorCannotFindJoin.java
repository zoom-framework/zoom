package org.zoomdev.zoom.dao.entities;


import org.zoomdev.zoom.dao.annotations.Join;
import org.zoomdev.zoom.dao.annotations.Link;
import org.zoomdev.zoom.dao.annotations.Table;

@Link({@Join(table = "shp_type", on = "tpId=typeId1")})
@Table("shp_product")
public class ErrorCannotFindJoin {
}
