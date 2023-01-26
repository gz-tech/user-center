package com.upuphone.cloudplatform.usercenter.remote.flashlogin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/27
 */
@Data
@JsonInclude(NON_NULL)
public class FlashLoginCommonRemoteRequest {

    private String appId;

    private String token;

    private String clientIp;

    private String encryptType;

    private String outId;

    private String sign;
}
