package com.upuphone.cloudplatform.usercenter.entity.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenBo extends TokenBo {
    private String refreshTokenId;
}
