package com.upuphone.cloudplatform.usercenter.auth.vo.invalidtoken;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel(value = "invalid access token response")
public class InvalidAccTokenResponse {

    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;

    @ApiModelProperty(value = "expirationTime", required = true)
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "startValidTime", required = true)
    private LocalDateTime startValidTime;
}
