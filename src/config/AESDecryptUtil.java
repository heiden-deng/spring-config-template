package com.heiden.dbp.zuul.config;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.encryption.StringEncryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: heiden
 * @program: cloud-platform-dbp-zuul
 * @description:
 * @create: 2018-08-08 18:37
 **/
public class AESDecryptUtil implements StringEncryptor {
    //加密
    @Override
    public String encrypt(String s) {
        String key = "test1234567890";
        byte[] raw = new byte[0];
        try {
            raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(s.getBytes("utf-8"));
            return new Base64().encodeToString(encrypted);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    //解密
    @Override
    public String decrypt(String sSrc) {
        String sKey = "test1234567890";
        try {
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
}