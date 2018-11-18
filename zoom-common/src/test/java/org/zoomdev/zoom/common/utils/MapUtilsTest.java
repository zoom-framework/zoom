package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtilsTest extends TestCase {

    public void test(){

        Map<String,Object> data = new HashMap<String, Object>();
        data.put("id","1");
        data.put("label","1");

        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        list.add(data);

        Map<String, Map<?, ?>> map  = MapUtils.toMap(list,"id");
    }
}
