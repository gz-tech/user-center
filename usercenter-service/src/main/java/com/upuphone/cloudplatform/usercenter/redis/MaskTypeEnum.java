package com.upuphone.cloudplatform.usercenter.redis;

public enum MaskTypeEnum {

    BOXING_ID("boxingid"),
    USER_ID("id"),
    MOBILE("mobile"),
    EMAIL("email"),
    ;

    private final String type;

    MaskTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
