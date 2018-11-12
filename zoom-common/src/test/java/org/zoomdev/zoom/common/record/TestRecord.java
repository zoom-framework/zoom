package org.zoomdev.zoom.common.record;

import junit.framework.TestCase;
import org.zoomdev.zoom.caster.Caster;
import org.zoomdev.zoom.common.utils.DataObject;

import static org.junit.Assert.assertEquals;

public class TestRecord extends TestCase {

    /**
     *
     */
    public void test(){
        DataObject data = DataObject.as(
                "id",
                "testId"
        );

        assertEquals(Caster.to(new MockClob("{\"id\":\"testId\"}"),DataObject.class),
                data );

    }
}
