package com.upuphone.cloudplatform.usercenter.entity.token;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author guangzheng.ding
 * @date 2021/12/31 15:47
 */
@Getter
@Setter
public class TokenBo {
    private String userId;

    private String deviceId;

    private String deviceType;

    private String deviceName;

    private String model;

    private String appid;

    private Boolean expired;

    private Long expireTimeLength;

    private LocalDateTime startValidTime;

    private LocalDateTime expireTime;

    private String tokenStr;// TODO now only used in refresh token decrypted
}
