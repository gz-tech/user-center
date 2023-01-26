package com.upuphone.cloudplatform.usercenter.remote.wechat.token.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upuphone.cloudplatform.usercenter.remote.wechat.model.WeChatErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatTokenResponse extends WeChatErrorResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("openid")
    private String openId;

    private String scope;
}
