package com.upuphone.cloudplatform.usercenter.remote.token.model;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/10
 */
public interface TokenValidateRequest {

    /**
     * 获取来源 如lotus
     *
     * @return 来源 如 lotus
     */
    String getSource();

    void setAccessToken(String accessToken);

    void setUserId(String userId);

    void setMobile(String mobile);

    /**
     * 获取要校验的token
     *
     * @return token
     */
    String getAccessToken();

    /**
     * 获取要校验的userId
     *
     * @return userId
     */
    String getUserId();

    /**
     * 获取要校验的手机号
     * @return mobile
     */
    String getMobile();
}
