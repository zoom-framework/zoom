package org.zoomdev.zoom.common.json;

import java.util.Map;

public interface JSONObject {

    String toJSON();

    void fromJSON(Map<String,Object> data);

}