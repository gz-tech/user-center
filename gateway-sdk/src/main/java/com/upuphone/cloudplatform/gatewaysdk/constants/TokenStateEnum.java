package com.upuphone.cloudplatform.gatewaysdk.constants;

import lombok.Getter;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/10
 */
@Getter
public enum TokenStateEnum {

    /**
     * 解析出现异常
     */
    ERROR(0),
    /**
     * 合法
     */
    VALID(1),
    /**
     * 非法
     */
    ILLEGAL(2),
    /**
     * 过期
     */
    EXPIRED(3),
    /**
     * 被强制过期
     */
    FORCE_INVALID(4),

    /**
     * 从另外一个设备上被登出
     */
    KICKED(5),

    ACCOUNT_DELETED(6),
    ;

    private final Integer state;

    TokenStateEnum(Integer state) {
        this.state = state;
    }
}
