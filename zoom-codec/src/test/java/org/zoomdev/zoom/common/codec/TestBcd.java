package org.zoomdev.zoom.common.codec;

import junit.framework.TestCase;

import java.util.Arrays;

public class TestBcd extends TestCase {


    public void test(){


        assertTrue(Arrays.equals(
                Bcd.str2bcd("030201"),
                new byte[]{0x03,0x02,0x01}
        ));

    }

}
