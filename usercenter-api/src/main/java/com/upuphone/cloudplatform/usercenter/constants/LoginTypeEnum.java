package com.upuphone.cloudplatform.usercenter.constants;

import java.util.HashMap;
import java.util.Map;

public enum LoginTypeEnum {

    PHONE_PWD("phone_password", null),
    PWD("password", null),
    PHONE_VALIDCODE("phone_validcode", ValidCodeType.LOGIN),
    SECURITY_PHONE_VALIDCODE("security_phone_validcode", ValidCodeType.SECURITY_PHONE_LOGIN),
    NEWDEVICE_PWD("newdevice_password", null),
    NEWDEVICE_VALIDCODE("newdevice_validcode", ValidCodeType.NEW_DEVICE_PHONE_LOGIN),
    LOGOUT("logout", null),
    DELETE_ACCOUNT("delete_account", null),
    ACCOUNT_EMAIL_PWD("account_email_pwd", null),
    NEWDEVICE_EMAIL("newdevice_email", ValidCodeType.NEW_DEVICE_EMAIL),
    FLASH_LOGIN("flash_login", null),
    THIRD_PART_LOGIN("third_part_login", null);
    private final String type;
    private final ValidCodeType validCodeType;

    LoginTypeEnum(String type, ValidCodeType validCodeType) {
        this.type = type;
        this.validCodeType = validCodeType;
    }

    public ValidCodeType getValidCodeType() {
        return validCodeType;
    }

    public String getType() {
        return type;
    }

    private static final Map<String, LoginTypeEnum> TYPE_MAP = new HashMap<>();

    static {
        for (LoginTypeEnum value : values()) {
            TYPE_MAP.put(value.getType(), value);
        }
    }

    public static LoginTypeEnum getByType(String type) {
        return TYPE_MAP.get(type);
    }
}
