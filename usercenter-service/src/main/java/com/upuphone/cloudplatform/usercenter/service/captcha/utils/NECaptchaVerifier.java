package com.upuphone.cloudplatform.usercenter.service.captcha.utils;

import com.upuphone.cloudplatform.common.utils.JsonUtility;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.NESecretPair;
import com.upuphone.cloudplatform.usercenter.service.captcha.entity.VerifyResult;
import com.upuphone.cloudplatform.usercenter.setting.CaptchaSetting;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 二次验证
 */
@Slf4j
@Component
@Data
@NoArgsConstructor
public class NECaptchaVerifier {
    @Autowired
    private CaptchaSetting captchaSetting;
    @Autowired
    private RestTemplate restTemplate;

    private static final String VERSION = "v2";
    private String captchaId = ""; // 验证码id
    private NESecretPair secretPair = null; // 密钥对

    public NECaptchaVerifier(String captchaId, NESecretPair secretPair) {
        Validate.notBlank(captchaId, "captchaId为空");
        Validate.notNull(secretPair, "secret为null");
        Validate.notBlank(secretPair.secretId, "secretId为空");
        Validate.notBlank(secretPair.secretKey, "secretKey为空");
        this.captchaId = captchaId;
        this.secretPair = secretPair;
    }

    /**
     * 二次验证
     *
     * @param validate 验证码组件提交上来的NECaptchaValidate值
     * @param user     用户
     * @return VerifyResult verifyResult
     */
    public VerifyResult verify(String validate, String user) {
        if (StringUtils.isEmpty(validate) || StringUtils.equals(validate, "null")) {
            return VerifyResult.fakeFalseResult("validate data is empty");
        }
        // bugfix:如果user为null会出现签名错误的问题
        user = (user == null) ? "" : user;
        Map<String, String> params = new HashMap<String, String>();
        params.put("captchaId", captchaId);
        params.put("validate", validate);
        params.put("user", user);
        // 公共参数
        params.put("secretId", secretPair.secretId);
        params.put("version", VERSION);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", String.valueOf(ThreadLocalRandom.current().nextInt()));
        // 计算请求参数签名信息
        String signature = sign(secretPair.secretKey, params);
        params.put("signature", signature);
        String resp = "";
        try {
            resp = CaptchaHttpUtil.readContentFromPost(captchaSetting.getCaptChaUrl(), params);
        } catch (IOException ex) {
            log.error("http connect occur exception,please check !");
        }
        log.info("resp = " + resp);
        return verifyRet(resp);
    }

    /**
     * 生成签名信息
     *
     * @param secretKey 验证码私钥
     * @param params    接口请求参数名和参数值map，不包括signature参数名
     * @return String String
     */
    public static String sign(String secretKey, Map<String, String> params) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(key).append(params.get(key));
        }
        sb.append(secretKey);
        try {
            return DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // 一般编码都支持的。。
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证返回结果<br>
     * 1. 当易盾服务端出现异常或者返回异常时，优先使用返回true的结果，反之阻塞用户的后序操作<br>
     * 2. 如果想修改为返回false结果。可以调用VerifyResult.fakeFalseResult(java.lang.String)函数
     *
     * @param resp
     * @return VerifyResult VerifyResult
     */
    private VerifyResult verifyRet(String resp) {
        if (StringUtils.isEmpty(resp)) {
            return VerifyResult.fakeTrueResult("return empty response");
        }
        try {
            VerifyResult verifyResult = JsonUtility.toObject(resp, VerifyResult.class);
            return verifyResult;
        } catch (Exception ex) {
            log.info("yidun captcha return error response ,please check!");
            ex.printStackTrace();
            return VerifyResult.fakeTrueResult(resp);
        }
    }
}
