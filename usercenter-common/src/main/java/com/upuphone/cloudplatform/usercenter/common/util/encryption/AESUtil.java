package com.upuphone.cloudplatform.usercenter.common.util.encryption;

import com.upuphone.cloudplatform.common.exception.ServiceException;
import com.upuphone.cloudplatform.common.response.CommonErrorCode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;


public class AESUtil {
    // 共通鍵
    private static final String ENCRYPTION_KEY = "RwcmlVtt";
    private static final String ENCRYPTION_IV = "4e5Wa71fYoT7ktlg";

    public static String encrypt(String src, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(key), makeIv(iv));
            return Base64.getEncoder().encodeToString(cipher.doFinal(src.getBytes()));
        } catch (Exception e) {
            throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
        }
    }

    public static String decrypt(String src, String key, String iv) {
        String decrypted = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(key), makeIv(iv));
            decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(src)));
        } catch (Exception e) {
            throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
        }
        return decrypted;
    }

    public static AlgorithmParameterSpec makeIv(String ivStr) {
        try {
            return new IvParameterSpec(ivStr.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
        }
    }

    public static Key makeKey(String keyStr) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] key = md.digest(keyStr.getBytes("UTF-8"));
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(CommonErrorCode.SERVICE_ERROR, e);
        }
    }
}
