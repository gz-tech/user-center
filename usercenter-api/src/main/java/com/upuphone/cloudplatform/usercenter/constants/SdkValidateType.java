package com.upuphone.cloudplatform.usercenter.constants;


public enum SdkValidateType {
    PHONE_VALID_CODE(1, "手机验证码"),
    EMAIL_VALID_CODE(2, "邮箱验证码");

    SdkValidateType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private final int type;
    private final String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
