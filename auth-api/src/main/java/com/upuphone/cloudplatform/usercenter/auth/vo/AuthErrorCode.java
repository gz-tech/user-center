package com.upuphone.cloudplatform.usercenter.auth.vo;

import com.upuphone.cloudplatform.common.response.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    TOKEN_INVALID_USER_LOGOUT(301001, "token失效-用户登出"),
    TOKEN_INVALID_EXPIRATION(301002, "token失效-已过期"),
    APPROVE_STATE_NOT_FOUND(301003, "用户未授权"),
    APPROVE_STATE_CANCELLED(301004, "用户取消授权"),
    CODE_EXPIRED(301005, "授权码已过期"),
    APP_NOT_REGISTERED(301006, "应用未注册"),
    INVALID_REDIRECT_URI(301007, "redirect_uri非法"),
    APP_OAUTH_FORBIDDEN(301008, "禁止该应用使用oauth登录"),
    INVALID_GRANT_TYPE(301009, "grant_type非法"),
    USER_MOBILE_NOT_MATCH(301010, "登录用户手机和手机号不匹配！"),
    USER_NOT_FOUND(301011, "用户不存在！"),
    ;


    private final int errorCode;
    private final String errorMessage;


    AuthErrorCode(int errorCode, String errorMessage) {
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
