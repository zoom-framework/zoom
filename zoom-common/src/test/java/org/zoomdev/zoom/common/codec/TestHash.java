package org.zoomdev.zoom.common.codec;

import junit.framework.TestCase;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;

public class TestHash extends TestCase {

    public void test() {

        assertEquals(HashStr.md5("测试"),
                DigestUtils.md5Hex("测试"));


        assertEquals(HashStr.sha1("测试"),
                DigestUtils.sha1Hex("测试"));


        assertEquals(HashStr.sha256("测试"),
                DigestUtils.sha256Hex("测试"));


        assertEquals(HashStr.sha512("测试"),
                DigestUtils.sha512Hex("测试"));


    }

    public void testHex() {
        assertTrue(
                Arrays.equals(Hash.md5("测试".getBytes()),
                        DigestUtils.md5("测试")
                ));


        assertTrue(
                Arrays.equals(Hash.sha1("测试".getBytes()),
                        DigestUtils.sha1("测试")));


        assertTrue(
                Arrays.equals(Hash.sha256("测试".getBytes()),
                        DigestUtils.sha256("测试")));


        assertTrue(
                Arrays.equals(Hash.sha512("测试".getBytes()),
                        DigestUtils.sha512("测试")));
    }
}
