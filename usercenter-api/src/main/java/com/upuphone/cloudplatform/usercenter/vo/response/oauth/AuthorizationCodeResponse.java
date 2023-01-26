package com.upuphone.cloudplatform.usercenter.vo.response.oauth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "获取AuthorizationCode response")
public class AuthorizationCodeResponse {
    @ApiModelProperty(value = "认证 code", required = true)
    private String code;
}
