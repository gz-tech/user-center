package com.upuphone.cloudplatform.usercenter.service.authentication.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenPersistenceResult {
    private String accessToken;
    private String refreshToken;
}
