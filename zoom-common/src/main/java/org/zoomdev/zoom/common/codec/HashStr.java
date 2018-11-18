package org.zoomdev.zoom.common.codec;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * DigestUtils 太多同名的方法，在实际应用中，万一有个参数是接受的Object，容易出低级错误
 * 用 {@link Hash} 和 {@link HashStr} 分别代表二进制和String版本避免低级错误
 */
public class HashStr {

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String sha256(String str) {
        return DigestUtils.sha256Hex(str);
    }

    public static String sha1(String str) {
        return DigestUtils.sha1Hex(str);
    }


    public static String sha512(String str) {
        return DigestUtils.sha512Hex(str);
    }
}
