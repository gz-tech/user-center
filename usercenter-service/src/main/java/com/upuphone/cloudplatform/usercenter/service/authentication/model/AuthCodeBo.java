package com.upuphone.cloudplatform.usercenter.service.authentication.model;

import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/4/29
 */
@Data
public class AuthCodeBo {

    private String code;
    private String scope;
    private String deviceId;
    private String model;
    private String deviceName;
    private String deviceType;
    private String appId;
    private Long userId;
}
