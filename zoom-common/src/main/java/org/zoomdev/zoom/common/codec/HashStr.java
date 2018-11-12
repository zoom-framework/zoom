package org.zoomdev.zoom.common.codec;

import org.apache.commons.codec.digest.DigestUtils;

public class HashStr {

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String sha256(String str) {
        return DigestUtils.sha256Hex(str);
    }
}
