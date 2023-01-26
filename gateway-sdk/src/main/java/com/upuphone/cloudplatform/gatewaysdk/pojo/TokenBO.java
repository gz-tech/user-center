package com.upuphone.cloudplatform.gatewaysdk.pojo;

import lombok.Data;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/10
 */
@Data
public class TokenBO {

    private String appid;

    private String userId;

    private String deviceId;

    /**
     * 0-PC, 1-手机,2-平板,3-移动web,4-car,5-vr,-1=未知
     */
    private String deviceType;

    /**
     * end with second
     */
    private Long expireTime;


    private String refreshTokenId;
}
