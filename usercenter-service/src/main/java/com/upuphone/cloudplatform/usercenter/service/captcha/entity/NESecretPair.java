package com.upuphone.cloudplatform.usercenter.service.captcha.entity;

/**
 * 密钥对
 */
public class NESecretPair {
    public final String secretId;
    public final String secretKey;

    /**
     * 构造函数
     *
     * @param secretId  密钥对id
     * @param secretKey 密钥对key
     */
    public NESecretPair(String secretId, String secretKey) {
        this.secretId = secretId;
        this.secretKey = secretKey;
    }
}
