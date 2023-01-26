package com.upuphone.cloudplatform.usercenter.constants;
import java.util.HashMap;
import java.util.Map;
/**
 * @Classname CaptchaBusinussEnum
 * @Description 人机检测业务类型
 * @Date 2022/6/1 10:36 上午
 * @Created by gz-d
 */
public enum CaptchaBusinessEnum {
    SECURITY_MODULE("security", "安全中心模块"),
    REGIESTER_MODULE("register", "注册模块");

    CaptchaBusinessEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
    private final String type;
    private final String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, CaptchaBusinessEnum> TYPE_MAP = new HashMap<>();

    static {
        for (CaptchaBusinessEnum value : values()) {
            TYPE_MAP.put(value.getType(), value);
        }
    }
    public static CaptchaBusinessEnum getByType(String type) {
        return TYPE_MAP.get(type);
    }
}
