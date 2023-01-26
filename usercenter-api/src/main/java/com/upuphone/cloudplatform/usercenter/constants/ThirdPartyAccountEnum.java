package com.upuphone.cloudplatform.usercenter.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ThirdPartyAccountEnum {
    WE_CHAT(1, "wechatLoginServiceImpl"),
    ;

    private static final Map<Integer, ThirdPartyAccountEnum> TYPE_MAP = new HashMap<>();

    static {
        for (ThirdPartyAccountEnum value : values()) {
            TYPE_MAP.put(value.getType(), value);
        }
    }

    private final int type;
    private final String name;

    ThirdPartyAccountEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static ThirdPartyAccountEnum getByType(int type) {
        return TYPE_MAP.get(type);
    }

    public static Set<Integer> getAllTypes() {
        return TYPE_MAP.keySet();
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
