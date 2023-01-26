package com.upuphone.cloudplatform.usercenter.service.userbasic.login.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdPartyAccessTokenBo {
    private String accessToken;

    private String openId;
}
