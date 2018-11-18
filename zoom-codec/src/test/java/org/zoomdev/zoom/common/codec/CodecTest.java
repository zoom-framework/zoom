package org.zoomdev.zoom.common.codec;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.UnsupportedEncodingException;

public class CodecTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CodecTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CodecTest.class);
    }

    public void test() throws UnsupportedEncodingException {

        String base64 = Codec.encodeBase64("艰苦奋斗健身房");
        assertEquals("艰苦奋斗健身房", Codec.decodeBase64(base64));

        base64 = Codec.encodeBase64("今飞凯达急速开发".getBytes("utf-8"));


    }


}
