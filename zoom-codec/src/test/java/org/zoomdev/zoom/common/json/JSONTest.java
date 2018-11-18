package org.zoomdev.zoom.common.json;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JSONTest {

    @Test
    public void test() {

        JSON.stringify(new HashMap());
        JSON.parse("{}", Map.class);
    }
}
