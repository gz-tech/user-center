package com.upuphone.cloudplatform.usercenter.service.basic.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.upuphone.cloudplatform.common.exception.BusinessException;
import com.upuphone.cloudplatform.usercenter.errorcode.UserCenterErrorCode;
import com.upuphone.cloudplatform.usercenter.service.util.HttpClient4Utils;
import com.upuphone.cloudplatform.usercenter.service.util.SignatureUtils;
import com.upuphone.cloudplatform.usercenter.vo.request.basic.CheckTextRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

/**
 * @Author Min.Jiang
 * @Date 2022/5/30 21:48
 * @Version 1.0
 */

@Component
@Slf4j
public class CheckTextUtil {

    /**
     * 产品密钥ID，产品标识
     */
    private final static String SECRETID = "3bf40ce9e8210c19ad4d82780bc1a0c0";
    /**
     * 产品私有密钥，服务端生成签名信息使用，请严格保管，避免泄露
     */
    private final static String SECRETKEY = "f256717e573e0407c753cbd29e9708c0";
    /**
     * 业务ID，易盾根据产品业务特点分配
     */
    private final static String BUSINESSID = "ebf5b2f62d4070546c0b0233cf2d61bb";
    /**
     * 易盾反垃圾云服务文本在线检测接口地址
     */
    private final static String API_URL = "http://as.dun.163.com/v3/text/check";
    /**
     * 实例化HttpClient，发送http请求使用，可根据需要自行调参
     */
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 2000, 2000, 2000);


    public void textValidation(CheckTextRequest request) {
        HashMap<String, String> params = new HashMap<>();
        // 1.设置公共参数
        params.put("secretId", SECRETID);
        params.put("businessId", BUSINESSID);
        params.put("version", "v3.1");
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("nonce", String.valueOf(new Random().nextInt()));
        params.put("signatureMethod", "MD5"); // MD5, SM3, SHA1, SHA256

        // 2.设置私有参数
        params.put("dataId", request.getDataId());
        params.put("content", request.getContent());

        // 3.生成签名信息
        String signature = SignatureUtils.genSignature(SECRETKEY, params);
        params.put("signature", signature);

        // 4.发送HTTP请求，这里使用的是HttpClient工具包，产品可自行选择自己熟悉的工具包发送请求
        String response = HttpClient4Utils.sendPost(httpClient, API_URL, params, Consts.UTF_8);

        // 5.解析接口返回值
        JsonObject jObject = new JsonParser().parse(response).getAsJsonObject();
        int code = jObject.get("code").getAsInt();
        String msg = jObject.get("msg").getAsString();
        if (code == 200) {
            JsonObject resultObject = jObject.getAsJsonObject("result");
            String taskId = resultObject.get("taskId").getAsString();
            int action = resultObject.get("action").getAsInt();
            if (action == 0) {
                log.info(String.format("taskId=%s，文本机器检测结果：通过", taskId));
            } else {
                throw new BusinessException(UserCenterErrorCode.TEXT_VALIDATE_ERROR, "The name is not allowed");
            }
        } else {
            log.error(String.format("ERROR: code=%s, msg=%s", code, msg));
        }
    }
}
