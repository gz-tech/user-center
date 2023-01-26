package com.upuphone.cloudplatform.gatewaysdk.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenHeaderBO {

    private String appid;

    /**
     * 用户信息
     */
    private Long userId;

    /**
     * 登录设备ID
     */
    private String deviceId;

    /**
     * 0-PC, 1-手机,2-平板,3-移动web,4-car,5-vr,-1=未知
     */
    private String deviceType;

    /**
     * 过期时间 end with second
     */
    private Long expirationTime;


    private String refreshTokenId;
}
