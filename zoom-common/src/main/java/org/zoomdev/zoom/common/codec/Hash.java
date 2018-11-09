package org.zoomdev.zoom.common.codec;

import org.apache.commons.codec.digest.DigestUtils;

public class Hash {
	public static byte[] md5(byte[] src) {
		return DigestUtils.md5(src);
	}
	
	
}
