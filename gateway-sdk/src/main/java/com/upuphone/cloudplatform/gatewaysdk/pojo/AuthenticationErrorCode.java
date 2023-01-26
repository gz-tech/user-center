package com.upuphone.cloudplatform.gatewaysdk.pojo;

import com.upuphone.cloudplatform.common.response.ErrorCode;

/**
 * @author guangzheng.ding
 * @date 2021/12/17 15:27
 */
public enum AuthenticationErrorCode implements ErrorCode {
    //
    TOKEN_EMPTY_ERROR(800001, "用户凭证为空"),
    TOKEN_PARSE_ERROR(800002, "用户凭证解析失败"),
    TOKEN_ILLEGAL_ERROR(800003, "用户凭证不合法"),
    TOKEN_EXPIRED_ERROR(800004, "用户凭证已过期"),
    TOKEN_FORCE_INVALID_ERROR(800005, "用户凭证已失效"),
    TOKEN_DEVICE_FORCE_LOGOUT_ERROR(800008, "用户设备已登出"),
    ACCOUNT_DELETED_ERROR(800009, "帐号已注销");

    private int errorCode;
    private String errorMessage;


    AuthenticationErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
