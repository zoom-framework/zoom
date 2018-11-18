package org.zoomdev.zoom.common.codec;

import junit.framework.TestCase;

import java.util.Arrays;

public class TestHex extends TestCase {

    public void test() {

        assertTrue(Arrays.equals(
                Hex.decodeHex("0102030e"),
                new byte[]{01, 02, 03, 0x0e}
        ));

        assertTrue(Arrays.equals(
                Hex.decodeHex("0102030e".toCharArray()),
                new byte[]{01, 02, 03, 0x0e}
        ));

        byte[] bytes = new byte[4];
        Hex.decodeHex("0102030e".toCharArray(), bytes, 0);
        assertTrue(Arrays.equals(
                bytes,
                new byte[]{01, 02, 03, 0x0e}
        ));


        bytes = new byte[4];
        Hex.decodeHex("0102030e", bytes, 0);
        assertTrue(Arrays.equals(
                bytes,
                new byte[]{01, 02, 03, 0x0e}
        ));


        assertTrue(
                Arrays.equals(
                        Hex.encodeHex(new byte[]{01, 02, 03, 0x0e}),
                        new char[]{
                                '0', '1', '0', '2', '0', '3', '0', 'e'
                        }
                )
        );

        assertEquals(
                Hex.encodeHexStr(new byte[]{01, 02, 03, 0x0e}),
                "0102030e"
        );

        assertEquals(
                Hex.encodeHexStr(new byte[]{01, 02, 03, 0x0e}, 4),
                "0102030e"
        );
        assertEquals(
                Hex.encodeHexStr(new byte[]{01, 02, 03, 0x0e}, 1, 3),
                "02030e"
        );

    }
}
