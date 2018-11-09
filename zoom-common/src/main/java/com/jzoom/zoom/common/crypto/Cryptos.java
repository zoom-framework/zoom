package com.jzoom.zoom.common.crypto;

public class Cryptos {
	
	/**
	 * 注意key的长度必须为16/32  , 需要使用32位秘钥则要升级oracle的许可jar包
	 */
	public static final String AES_ECB_PKCS5 = "AES/ECB/PKCS5Padding";

	
	
	/**
	 * 
	 * AES/ECB/PKCS5Padding
	 * 
	 * 线程安全
	 * @param name
	 * @return
	 */
	public static Crypto get(String name) {
		return new JCrypto(name);
		
	}
}
