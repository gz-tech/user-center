package com.upuphone.cloudplatform.usercenter.service.authentication.model;

import com.upuphone.cloudplatform.common.response.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckAccessTokenResult {

    private String accessToken;

    private LocalDateTime expirationTime;

    private LocalDateTime startValidTime;

    private ErrorCode errorCode;

    private String userId;

    private boolean isExpired;
}
