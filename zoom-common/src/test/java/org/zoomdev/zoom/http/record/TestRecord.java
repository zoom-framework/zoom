package org.zoomdev.zoom.http.record;

import junit.framework.TestCase;
import org.zoomdev.zoom.http.caster.Caster;
import org.zoomdev.zoom.http.utils.DataObject;

public class TestRecord extends TestCase {

    /**
     *
     */
    public void test() {
        DataObject data = DataObject.as(
                "id",
                "testId"
        );

        assertEquals(Caster.to(new MockClob("{\"id\":\"testId\"}"), DataObject.class),
                data);

    }
}
