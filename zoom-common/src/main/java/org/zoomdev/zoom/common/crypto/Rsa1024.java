package org.zoomdev.zoom.common.crypto;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class Rsa1024 implements Rsa {

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public Rsa1024() {

    }

    @Override
    public boolean verify(byte[] content, byte[] publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content);
            return signature.verify(content);

        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public byte[] sign(byte[] content, byte[] privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content);

            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
