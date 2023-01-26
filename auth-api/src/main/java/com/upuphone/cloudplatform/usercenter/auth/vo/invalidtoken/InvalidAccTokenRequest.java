package com.upuphone.cloudplatform.usercenter.auth.vo.invalidtoken;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "invalid access token request")
public class InvalidAccTokenRequest {

    @ApiModelProperty(value = "accessToken", required = true)
    private String accessToken;
}
