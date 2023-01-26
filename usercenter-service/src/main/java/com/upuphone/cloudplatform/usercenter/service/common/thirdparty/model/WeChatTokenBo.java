package com.upuphone.cloudplatform.usercenter.service.common.thirdparty.model;

import lombok.Data;

@Data
public class WeChatTokenBo {

    private String accessToken;

    private String openId;
}
