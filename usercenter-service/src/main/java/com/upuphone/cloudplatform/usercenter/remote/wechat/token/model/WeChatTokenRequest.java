package com.upuphone.cloudplatform.usercenter.remote.wechat.token.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeChatTokenRequest {

    /**
     * 授权码
     */
    private String code;
}
