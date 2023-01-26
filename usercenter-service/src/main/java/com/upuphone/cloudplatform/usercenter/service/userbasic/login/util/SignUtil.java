package com.upuphone.cloudplatform.usercenter.service.userbasic.login.util;

import com.upuphone.cloudplatform.usercenter.common.util.encryption.ByteFormatUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * Description:签名工具类
 * User: liutao
 * Date: 2019-09-11
 * Time: 10:29
 */
@Slf4j
public class SignUtil {

    public static String getSign(Map<String, String> requestMap, String appKey) {
        return hmacSHA256Encrypt(requestMap2Str(requestMap), appKey);
    }


    private static String hmacSHA256Encrypt(String encryptText, String encryptKey) {
        byte[] result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA256");
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            byte[] rawHmac = mac.doFinal(encryptText.getBytes(StandardCharsets.UTF_8));
            return ByteFormatUtil.bytesToHexString(rawHmac);
        } catch (Exception e) {
            log.error("[SignUtil] 生成签名失败, e=", e);
            throw new RuntimeException(e);
        }
    }


    private static String requestMap2Str(Map<String, String> requestMap) {
        String[] keys = requestMap.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : keys) {
            if (!str.equals("sign")) {
                stringBuilder.append(str).append(requestMap.get(str));
            }
        }
        log.info("encryptText:{}", stringBuilder);
        return stringBuilder.toString();
    }
}
