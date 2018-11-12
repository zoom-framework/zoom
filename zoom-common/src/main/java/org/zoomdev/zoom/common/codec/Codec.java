package org.zoomdev.zoom.common.codec;

import org.zoomdev.zoom.caster.codec.Base64;

import java.io.UnsupportedEncodingException;

public class Codec {


    public static String encodeBase64(String src) {
        try {
            return Base64.encodeToString(src.getBytes("UTF-8"), false);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeBase64(byte[] src) {
        return Base64.encodeToString(src, false);
    }


    public static byte[] decodeBase64ToBytes(String src) {
        return Base64.decodeFast(src);
    }

    public static String decodeBase64(String src) {
        try {
            return new String(Base64.decodeFast(src), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
