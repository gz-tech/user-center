package com.upuphone.cloudplatform.usercenter.auth.vo.checktoken;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel(value = "CheckAccessTokenResponse")
public class CheckAccessTokenResponse {

    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;

    @ApiModelProperty(value = "expirationTime", required = true)
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "startValidTime", required = true)
    private LocalDateTime startValidTime;

    @ApiModelProperty(value = "startValidTime", required = true)
    private boolean isExpired;
}
