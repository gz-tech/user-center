package com.upuphone.cloudplatform.usercenter.remote.sms.model;

/**
 * type需要严格与ValidCodeType保持一致
 */
public enum SmsType {

    REGISTER(1, "REGISTER_XJ"),
    LOGIN(2, "LOGIN_XJ"),
    RESET_PASSWORD(3, "RESET_PASSWORD_XJ"),
    CHANGE_BIND_MOBILE(4, "MODIFY_BOUND_PHONE_XJ"),
    CHANGE_BIND_MOBILE_VALID(5, "MODIFY_BOUND_PHONE_XJ"),
    CHANGE_SECURITY_MOBILE_VALID(6, "MODIFY_SECURE_PHONE_XJ"),
    CHANGE_SECURITY_MOBILE(7, "MODIFY_SECURE_PHONE_XJ"),
    FORGOT_PASSWORD_VALID(8, "RETRIEVE_PASSWORD_XJ"),
    SECURITY_PHONE_LOGIN(9, "LOGIN_XJ"),
    NEW_DEVICE_LOGIN(10, "LOGIN_XJ"),
    CHANGE_BIND_EMAIL(11, "MODIFY_BOUND_EMAIL_XJ"),
    SDK_VALIDATE(14, "VALIDATE_OWNER_OPERATION_XJ"),
    SIMPLE_REGISTER_LOGIN(15, "SIMPLE_REGISTER_LOGIN_XJ"),
    AUTH_REMOVED(101, "RELEASED_FROM_CREDIT_XJ"),
    ;

    private final Integer type;
    private final String templateCode;

    SmsType(Integer type, String templateCode) {
        this.type = type;
        this.templateCode = templateCode;
    }

    public Integer getType() {
        return type;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public static SmsType getByType(Integer type) {
        for (SmsType smsType : SmsType.values()) {
            if (smsType.getType().equals(type)) {
                return smsType;
            }
        }
        return null;
    }

    public static boolean isInvalidType(Integer type) {
        return type < REGISTER.type || type > SIMPLE_REGISTER_LOGIN.type;
    }
}
