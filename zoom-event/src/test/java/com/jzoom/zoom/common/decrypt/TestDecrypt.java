package com.jzoom.zoom.common.decrypt;

import java.io.UnsupportedEncodingException;

import com.jzoom.zoom.common.crypto.Crypto;
import com.jzoom.zoom.common.crypto.Cryptos;

import junit.framework.TestCase;

public class TestDecrypt extends TestCase {

	public void test() throws UnsupportedEncodingException {
		Crypto crypto = Cryptos.get(Cryptos.AES_ECB_PKCS5);
		
		byte[] key = new byte[16];
		
		byte[] encrypted = crypto.encrypt("机开发惊呆了康师傅".getBytes("utf-8"),key); 
		
		assertTrue(new String(crypto.decrypt(encrypted, key)).equals("机开发惊呆了康师傅"));
		
	}
}
