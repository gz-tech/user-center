package com.upuphone.cloudplatform.usercenter.remote.wechat.userinfo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatUserInfoRequest {

    private String accessToken;

    private String openId;
}
