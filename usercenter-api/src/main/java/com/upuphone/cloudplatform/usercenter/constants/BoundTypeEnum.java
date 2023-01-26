package com.upuphone.cloudplatform.usercenter.constants;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 绑定类型（手机/安全手机/邮箱）
 *
 * @author hanzhumeng
 * Created: 2022/1/25
 */
@Getter
public enum BoundTypeEnum {

    SECURITY_PHONE(1),
    BOUND_PHONE(2),
    BOUND_EMAIL(3),
    ;

    private static final Map<Integer, BoundTypeEnum> TYPE_MAP = new HashMap<>();

    static {
        for (BoundTypeEnum value : values()) {
            TYPE_MAP.put(value.getType(), value);
        }
    }

    private final Integer type;

    BoundTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean isInvalidType(Integer type) {
        return type < 1 || type > 3;
    }

    public static BoundTypeEnum getByType(Integer type) {
        return TYPE_MAP.get(type);
    }
}
