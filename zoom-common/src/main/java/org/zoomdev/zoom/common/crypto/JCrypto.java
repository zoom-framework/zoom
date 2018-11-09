package org.zoomdev.zoom.common.crypto;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class JCrypto implements Crypto {


	private String name;
	private String ciper;
	
	
	public JCrypto(String ciper) {
		
		String[] parts = ciper.split("/");
		name = parts[0];
		this.ciper = ciper;
		
	}

	@Override
	public byte[] encrypt(byte[] data, byte[] key) {
		try {
			Key keySpec =  new SecretKeySpec(key, name);
			Cipher cipher = Cipher.getInstance(ciper);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			return cipher.doFinal(data);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] decrypt(byte[] data, byte[] key) {
		try {
			Key cKey = new SecretKeySpec(key, name);
			Cipher cipher = Cipher.getInstance(ciper);
			cipher.init(Cipher.DECRYPT_MODE, cKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
