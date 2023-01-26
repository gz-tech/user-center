package com.upuphone.cloudplatform.usercenter.remote.token.model;

/**
 * Description: 通用的第三方校验token返回结果接口
 *
 * @author hanzhumeng
 * Created: 2022/5/10
 */
public interface TokenValidateRemoteResponse {

    /**
     * 是否合法
     *
     * @return true/false
     */
    Boolean isValid();

    /**
     * 获取对应的第三方userId
     *
     * @return 第三方userId
     */
    String getUserId();
}
