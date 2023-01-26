package com.upuphone.cloudplatform.usercenter.remote.token.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/5/17
 */
@Getter
@Setter
@Accessors(chain = true)
public class LotusTokenValidateRemoteRequest {

    private String appId;

    private String redirectUrl;

    private Integer authType;
}
