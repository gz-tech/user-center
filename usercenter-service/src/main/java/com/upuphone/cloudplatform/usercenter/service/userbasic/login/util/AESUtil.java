package com.upuphone.cloudplatform.usercenter.service.userbasic.login.util;

import com.upuphone.cloudplatform.usercenter.common.util.encryption.ByteFormatUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


/**
 * Description: AES加密解密方法
 */
public class AESUtil {


    /**
     * Description: AES解密
     */
    public static String decrypt(String sSrc, String sKey, String siv) throws Exception {
        try {
            if (sSrc == null || sSrc.length() == 0) {
                return null;
            }
            if (sKey == null) {
                throw new Exception("decrypt key is null");
            }
            if (sKey.length() != 16) {
                throw new Exception("decrypt key length error");
            }
            byte[] decrypt = ByteFormatUtil.hexToBytes(sSrc);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec iv = new IvParameterSpec(siv.getBytes(StandardCharsets.UTF_8));//new IvParameterSpec(getIV());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);//使用解密模式初始化 密
            return new String(cipher.doFinal(decrypt), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new Exception("decrypt error", ex);
        }
    }
}
