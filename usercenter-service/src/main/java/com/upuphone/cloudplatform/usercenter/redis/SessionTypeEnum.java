package com.upuphone.cloudplatform.usercenter.redis;

public enum SessionTypeEnum {

    // 修改绑定手机号-session
    USER_CHANGE_BIND_MOBILE_SESSION("user_change_bind_mobile_session"),
    // 修改安全手机号-session
    USER_CHANGE_SECURITY_MOBILE_SESSION("user_change_security_mobile_session"),
    // 修改密码-session
    USER_CHANGE_PASSWORD_SESSION("user_change_password_session"),
    // 绑定安全手机号-session
    USER_BIND_SECURITY_MOBILE_SESSION("user_bind_security_mobile_session"),
    // 忘记密码-session
    USER_FORGOT_PASSWORD_SESSION("user_forgot_password_session"),
    // 修改/首次绑定邮箱-session
    USER_CHANGE_EMAIL_SESSION("user_change_email_session"),
    ;

    private final String key;

    SessionTypeEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
