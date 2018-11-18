package org.zoomdev.zoom.dao.entities;


import org.zoomdev.zoom.dao.annotations.Join;
import org.zoomdev.zoom.dao.annotations.Link;
import org.zoomdev.zoom.dao.annotations.Table;

@Link({@Join(table = "type", on = "tpId=typeId1")})
@Table("product")
public class ErrorCannotFindJoin {
}
