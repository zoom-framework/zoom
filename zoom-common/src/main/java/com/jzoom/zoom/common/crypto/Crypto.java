package com.jzoom.zoom.common.crypto;

public interface Crypto {
	
	/**
	 * Encript data with key to encripted
	 * @param data
	 * @param key
	 * @return
	 */
	byte[] encrypt(byte[] data,byte[] key);
	
	/**
	 * Decript base64 string with ke
	 * @param data
	 * @param key
	 * @return
	 */
	byte[] decrypt(byte[] data,byte[] key);
	

	

}
