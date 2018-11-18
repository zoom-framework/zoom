package org.zoomdev.zoom.common.codec;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * DigestUtils 太多同名的方法，在实际应用中，万一有个参数是接受的Object，容易出低级错误
 * 用 {@link Hash} 和 {@link HashStr} 分别代表二进制和String版本避免低级错误
 */
public class Hash {
    public static byte[] md5(byte[] src) {
        return DigestUtils.md5(src);
    }

    public static byte[] sha1(byte[] src) {
        return DigestUtils.sha1(src);
    }

    public static byte[] sha256(byte[] src) {
        return DigestUtils.sha256(src);
    }

    public static byte[] sha512(byte[] src) {
        return DigestUtils.sha512(src);
    }
}
