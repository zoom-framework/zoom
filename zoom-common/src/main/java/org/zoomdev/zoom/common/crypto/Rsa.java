package org.zoomdev.zoom.common.crypto;

public interface Rsa {
	
	
	/**
	 * 使用公钥验证签名
	 * @param content
	 * @param publicKey
	 */
	boolean verify( byte[] content,byte[] publicKey );
	
	/***
	 * 使用私钥验签名
	 * @param content
	 * @param privateKey
	 */
	byte[] sign(byte[] content,byte[] privateKey);
	

}
