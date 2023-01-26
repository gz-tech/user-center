package com.upuphone.cloudplatform.usercenter.api.constant;

public enum DeviceTypeEnum {
    /**
     *
     */
    UNKNOW(-1, "未知"),
    PC(0, "PC"),
    PHONE(1, "手机"),
    PAD(2, "平板"),
    MOBILE_WEB(3, "移动web"),
    CAR(4, "汽车"),
    VR(5, "VR"),
    ;


    private final int type;
    private final String name;

    DeviceTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static DeviceTypeEnum getByType(int type) {
        for (DeviceTypeEnum value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
